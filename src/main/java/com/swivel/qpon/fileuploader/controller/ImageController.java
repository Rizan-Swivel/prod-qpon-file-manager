package com.swivel.qpon.fileuploader.controller;

import com.swivel.qpon.fileuploader.domain.request.ImageRequestDto;
import com.swivel.qpon.fileuploader.domain.response.ImageResponseDto;
import com.swivel.qpon.fileuploader.enums.ErrorResponseStatusType;
import com.swivel.qpon.fileuploader.enums.SuccessResponseStatusType;
import com.swivel.qpon.fileuploader.exception.InvalidFileException;
import com.swivel.qpon.fileuploader.service.ImageService;
import com.swivel.qpon.fileuploader.wrapper.ResponseWrapper;
import com.swivel.qpon.fileuploader.configurations.Translator;
import com.swivel.qpon.fileuploader.exception.FileManagerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * Images Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/images")
public class ImageController extends Controller {

    private static final String IMAGE_JPEG = "image/jpeg";
    private static final String IMAGE_PNG = "image/png";
    private static final String IMAGE_UPLOAD_PARAM_NAME = "image";
    private final long maxImageByteSize;
    private final ImageService imageService;

    @Autowired
    public ImageController(@Value("${image.maxByteSize}") long maxImageByteSize,
                           ImageService imageService,
                           Translator translator) {
        super(translator);
        this.maxImageByteSize = maxImageByteSize;
        this.imageService = imageService;
    }

    /**
     * This method generates the image url for the given image.
     *
     * @param request MultipartHttpServletRequest
     * @return image url
     */
    @PostMapping(path = {"/upload"},
            consumes = MULTIPART_FORM_DATA, produces = APPLICATION_JSON)
    public ResponseEntity<ResponseWrapper> uploadImage(
            @RequestParam(required = false) String uploadName, MultipartHttpServletRequest request) {

        if (request.getFile(IMAGE_UPLOAD_PARAM_NAME) == null ||
                request.getFile(IMAGE_UPLOAD_PARAM_NAME).getSize() == 0) {
            return getBadRequestError(ErrorResponseStatusType.MISSING_REQUIRED_FIELDS);
        }

        if (uploadName != null && !isValidUploadName(uploadName)) {
            return getBadRequestError(ErrorResponseStatusType.INVALID_UPLOAD_NAME);
        }

        MultipartFile multipartFile = request.getFile(IMAGE_UPLOAD_PARAM_NAME);
        String imageName = multipartFile.getOriginalFilename();
        log.debug("Image uploading request for image name:{}, uploadName:{}", imageName, uploadName);
        try {
            String imageContentType = multipartFile.getContentType();
            long imageByteSize = multipartFile.getSize();
            ImageResponseDto imageResponseDto = new ImageResponseDto(multipartFile, null);
            if (imageContentType != null && (imageContentType.equals(IMAGE_JPEG) || imageContentType.equals(IMAGE_PNG))) {
                if (imageByteSize <= maxImageByteSize) {
                    return uploadImageIfValid(multipartFile, uploadName);
                } else {
                    return getBadRequestError(ErrorResponseStatusType.EXCEEDED_IMAGE_SIZE, imageResponseDto);
                }
            } else {
                return getBadRequestError(ErrorResponseStatusType.INVALID_IMAGE_TYPE, imageResponseDto);
            }
        } catch (FileManagerException e) {
            log.error("Uploading image was failed for the image name: {}", imageName, e);
            return getInternalServerError();
        }
    }

    /**
     * This method deletes image.
     *
     * @param imageRequestDto imageRequestDto
     * @return nothing
     */
    @DeleteMapping(path = {"/delete"},
            consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    public ResponseEntity<ResponseWrapper> deleteImage(@RequestBody ImageRequestDto imageRequestDto) {
        try {
            if (imageRequestDto.isRequiredAvailable()) {
                return deleteImageIfValid(imageRequestDto.getImageUrl());
            } else {
                return getBadRequestError(ErrorResponseStatusType.MISSING_REQUIRED_FIELDS);
            }
        } catch (FileManagerException e) {
            log.error("Deleting image was failed for the imageUrl: {}", imageRequestDto.getImageUrl(), e);
            return getInternalServerError();
        }
    }

    /**
     * This method uploads valid image.
     *
     * @param image image
     * @return image url
     */
    private ResponseEntity<ResponseWrapper> uploadImageIfValid(MultipartFile image, String uploadName) {
        String imageUrl = imageService.uploadImage(image, uploadName);
        ImageResponseDto imageResponseDto = new ImageResponseDto(image, imageUrl);
        log.debug("Successfully uploaded image: {}, imageUrl: {}", image.getOriginalFilename(),
                imageResponseDto.toLogJson());
        return getSuccessResponse(SuccessResponseStatusType.IMAGE_UPLOAD, imageResponseDto);
    }

    /**
     * This method deletes image if valid imageUrl is given.
     *
     * @param imageUrl imageUrl
     * @return nothing
     */
    private ResponseEntity<ResponseWrapper> deleteImageIfValid(String imageUrl) {
        try {
            imageService.deleteImage(imageUrl);
            log.debug("Successfully deleted image. imageUrl: {}", imageUrl);
            return getSuccessResponse(SuccessResponseStatusType.IMAGE_DELETE, null);
        } catch (InvalidFileException e) {
            return getBadRequestError(ErrorResponseStatusType.INVALID_IMAGE_URL);
        }
    }

    /**
     * This method checks the validity of upload file name param
     *
     * @param uploadName uploadName
     * @return true/false
     */
    private boolean isValidUploadName(String uploadName) {
        return uploadName.matches("^[_A-z0-9]*((-)*[_A-z0-9])*$");
    }

}
