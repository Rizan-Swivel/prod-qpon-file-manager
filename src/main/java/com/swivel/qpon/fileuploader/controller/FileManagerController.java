package com.swivel.qpon.fileuploader.controller;

import com.swivel.qpon.fileuploader.domain.entity.FileManager;
import com.swivel.qpon.fileuploader.domain.request.FileUpdateRequestDto;
import com.swivel.qpon.fileuploader.domain.response.FileDetailResponseDto;
import com.swivel.qpon.fileuploader.domain.response.FileListResponse;
import com.swivel.qpon.fileuploader.domain.response.FileResponseDto;
import com.swivel.qpon.fileuploader.domain.response.FileSummaryPageResponse;
import com.swivel.qpon.fileuploader.enums.ErrorResponseStatusType;
import com.swivel.qpon.fileuploader.enums.SuccessResponseStatusType;
import com.swivel.qpon.fileuploader.exception.InvalidFileException;
import com.swivel.qpon.fileuploader.service.FileManagerService;
import com.swivel.qpon.fileuploader.wrapper.ResponseWrapper;
import com.swivel.qpon.fileuploader.configurations.Translator;
import com.swivel.qpon.fileuploader.exception.FileManagerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/api/v1/files")
public class FileManagerController extends Controller {

    private static final String DEFAULT_TYPE = "NONE";
    private static final String USER_ID = "User-Id";
    private static final int PAGE_MAX_SIZE = 250;
    private static final int DEFAULT_PAGE = 0;
    private static final String SEPARATOR = "-";
    private final long maxFileByteSize;
    private final FileManagerService fileManagerService;
    private final List<String> fileTypes;
    private final int maxFileCount;

    @Autowired
    public FileManagerController(FileManagerService fileManagerService,
                                 @Value("${file.maxByteSize}") long maxFileByteSize,
                                 @Value("${file.types}") List<String> fileTypes,
                                 @Value("${file.count}") int maxFileCount,
                                 Translator translator) {
        super(translator);
        this.fileManagerService = fileManagerService;
        this.maxFileByteSize = maxFileByteSize;
        this.fileTypes = fileTypes;
        this.maxFileCount = maxFileCount;
    }

    /**
     * This method generate file Url and upload file to s3
     *
     * @param files    file
     * @param fileName fileName
     * @return fileUrl
     */
    @PostMapping(path = "/upload")
    public ResponseEntity<ResponseWrapper> uploadFile(@RequestHeader(value = USER_ID) String userId,
                                                      @RequestParam("files") List<MultipartFile> files,
                                                      @RequestParam(required = false) String fileName) {
        try {
            log.info("file size {} ", files.size());
            if (!files.isEmpty()) {
                for (MultipartFile file: files) {
                    log.info("File Name : {} ", file.getOriginalFilename());
                }
            }

            int fileCount = files.size();
            if (fileCount > maxFileCount) {
                return getBadRequestError(ErrorResponseStatusType.MAX_FILE_COUNT);
            }
            if (!validateFileTypes(files)) {
                return getBadRequestError(ErrorResponseStatusType.UNSUPPORTED_FILE_FORMAT);
            }
            List<FileResponseDto> fileResponseList = new ArrayList<>();
            int fileNo = 1;
            for (MultipartFile file : files) {
                long byteSize = file.getSize();
                if (byteSize <= maxFileByteSize) {
                    getFileResponseList(fileResponseList, file, fileName, fileNo, userId, fileCount);
                } else {
                    return getBadRequestError(ErrorResponseStatusType.EXCEEDED_FILE_SIZE);
                }
                fileNo++;
            }
            FileListResponse fileListResponse = new FileListResponse(fileResponseList);
            return getSuccessResponse(SuccessResponseStatusType.FILE_UPLOAD, fileListResponse);
        } catch (FileManagerException e) {
            log.error("Uploading report was failed for the report name: {}", fileName, e);
            return getInternalServerError();
        }
    }

    /**
     * Validate the file formats
     *
     * @param files files
     * @return true/false
     */
    private boolean validateFileTypes(List<MultipartFile> files) {
        for (MultipartFile file : files) {
            if (file.getContentType() != null && fileTypes.contains(file.getContentType())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Download a file
     *
     * @param fileId fileId
     * @return a file
     */
    @GetMapping(path = "/download/{fileId}")
    public ResponseEntity<ByteArrayResource> downloadFile(@RequestHeader(value = USER_ID) String userId,
                                                          @PathVariable(value = "fileId") final String fileId) {
        try {
            String fileName = fileManagerService.getFileNameWithExtension(fileId, userId);
            final byte[] data = fileManagerService.downloadFile(fileName);
            final ByteArrayResource resource = new ByteArrayResource(data);
            return ResponseEntity
                    .ok()
                    .contentLength(data.length)
                    .header("Content-type", "application/octet-stream")
                    .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch (InvalidFileException | NoSuchFileException e) {
            return ResponseEntity
                    .notFound()
                    .build();
        } catch (FileManagerException e) {
            return ResponseEntity
                    .badRequest()
                    .contentLength(0)
                    .body(null);
        }
    }

    /**
     * Get file summary for a user
     *
     * @param userId userId
     * @param type   type
     * @param name   name
     * @param page   page
     * @param size   size
     * @return file summary
     */
    @GetMapping(path = "/summary/type/{type}/name/{name}/{page}/{size}",
            consumes = APPLICATION_JSON_UTF_8, produces = APPLICATION_JSON_UTF_8)
    public ResponseEntity<ResponseWrapper> getFileSummary(@RequestHeader(value = USER_ID) String userId,
                                                          @PathVariable("type") String type,
                                                          @PathVariable("name") String name,
                                                          @Min(DEFAULT_PAGE) @PathVariable("page") Integer page,
                                                          @Min(DEFAULT_PAGE) @Max(PAGE_MAX_SIZE)
                                                          @Positive @PathVariable("size") Integer size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            FileSummaryPageResponse fileSummary;
            if (type.equals(DEFAULT_TYPE) && name.equals(DEFAULT_TYPE)) {
                fileSummary = fileManagerService.getAllFileSummary(userId, pageable);
            } else {
                fileSummary = fileManagerService.getFileSummaryByNameOrType(userId, type, name, pageable);
            }
            return getSuccessResponse(SuccessResponseStatusType.FILE_SUMMARY, fileSummary);
        } catch (FileManagerException e) {
            log.error("Failed to get file summary:", e);
            return getInternalServerError();
        }
    }

    /**
     * Update file details
     *
     * @param userId               userId
     * @param fileUpdateRequestDto fileUpdateRequestDto
     * @return responseWrapper
     */
    @PutMapping(path = "", consumes = APPLICATION_JSON_UTF_8, produces = APPLICATION_JSON_UTF_8)
    public ResponseEntity<ResponseWrapper> updateFile(@RequestHeader(value = USER_ID) String userId,
                                                      @RequestBody FileUpdateRequestDto fileUpdateRequestDto) {
        try {
            if (fileUpdateRequestDto.isRequiredAvailable()) {
                fileManagerService.updateFile(fileUpdateRequestDto, userId);
                return getSuccessResponse(SuccessResponseStatusType.UPDATE_FILE, null);
            } else {
                return getBadRequestError(ErrorResponseStatusType.MISSING_REQUIRED_FIELDS);
            }
        } catch (InvalidFileException e) {
            log.error("Invalid file: {}", fileUpdateRequestDto.toLogJson(), e);
            return getBadRequestError(ErrorResponseStatusType.INVALID_FILE_ID);
        } catch (FileManagerException e) {
            log.error("Failed to update file: {} ", fileUpdateRequestDto.toLogJson(), e);
            return getInternalServerError();
        }
    }

    /**
     * Delete a file
     *
     * @param userId userId
     * @param fileId fileId
     * @return responseWrapper
     */
    @DeleteMapping(path = "{fileId}", consumes = APPLICATION_JSON_UTF_8, produces = APPLICATION_JSON_UTF_8)
    public ResponseEntity<ResponseWrapper> deleteFile(@RequestHeader(value = USER_ID) String userId,
                                                      @PathVariable("fileId") String fileId) {
        try {
            fileManagerService.deleteFile(fileId, userId);
            return getSuccessResponse(SuccessResponseStatusType.DELETE_FILE, null);
        } catch (InvalidFileException e) {
            log.error("Invalid file Id : {}", fileId, e);
            return getBadRequestError(ErrorResponseStatusType.INVALID_FILE_ID);
        } catch (FileManagerException e) {
            log.error("Failed to delete file : {}", fileId, e);
            return getInternalServerError();
        }
    }

    /**
     * Get file detail
     *
     * @param userId userId
     * @param fileId fileId
     * @return responseWrapper
     */
    @GetMapping(path = "{fileId}", consumes = APPLICATION_JSON_UTF_8, produces = APPLICATION_JSON_UTF_8)
    public ResponseEntity<ResponseWrapper> getFileDetail(@RequestHeader(value = USER_ID) String userId,
                                                         @PathVariable("fileId") String fileId) {
        try {
            FileManager fileDetail = fileManagerService.getFileDetail(fileId, userId);
            FileDetailResponseDto fileDetailResponseDto = new FileDetailResponseDto(fileDetail);
            return getSuccessResponse(SuccessResponseStatusType.FILE_DETAIL, fileDetailResponseDto);
        } catch (InvalidFileException e) {
            log.error("Invalid fileId : {}", fileId, e);
            return getBadRequestError(ErrorResponseStatusType.INVALID_FILE_ID);
        } catch (FileManagerException e) {
            log.error("Failed to get file detail: {}", fileId, e);
            return getInternalServerError();
        }
    }

    /**
     * File Response List
     *
     * @param fileResponseDtoList fileResponseDtoList
     * @param file                file
     * @param fileName            fileName
     * @param fileNo              fileNo
     * @param userId              userId
     * @param fileCount           fileCount
     */
    private void getFileResponseList(List<FileResponseDto> fileResponseDtoList, MultipartFile file,
                                     String fileName, int fileNo, String userId, int fileCount) {
        if (fileName == null) {
            fileName = file.getOriginalFilename();
        } else {
            fileName = getFileNames(fileCount, fileName, fileNo);
        }
        FileResponseDto fileResponseDto = fileManagerService.uploadFile(file, fileName, userId);
        fileResponseDtoList.add(fileResponseDto);
    }

    /**
     * @param fileCount fileCount
     * @param fileName  fileName
     * @param fileNo    fileNo
     * @return fileName
     */
    private String getFileNames(int fileCount, String fileName, int fileNo) {
        if (fileCount > 1) {
            fileName = fileName + SEPARATOR + fileNo;
        }
        return fileName;
    }

}
