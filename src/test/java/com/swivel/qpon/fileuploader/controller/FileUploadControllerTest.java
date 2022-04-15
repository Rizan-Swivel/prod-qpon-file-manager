package com.swivel.qpon.fileuploader.controller;

import com.swivel.qpon.fileuploader.domain.response.FileResponseDto;
import com.swivel.qpon.fileuploader.enums.ErrorResponseStatusType;
import com.swivel.qpon.fileuploader.enums.SuccessResponseStatusType;
import com.swivel.qpon.fileuploader.service.FileManagerService;
import com.swivel.qpon.fileuploader.configurations.ResourceBundleMessageSourceBean;
import com.swivel.qpon.fileuploader.configurations.Translator;
import com.swivel.qpon.fileuploader.exception.FileManagerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * This class tests the {@link FileManagerController} class
 */
class FileUploadControllerTest {

    private static final String USER_ID = "uid-d9d069f9-2b07-446f-8117-e78ff14d01d8";
    public static final String X_USER_ID = "x-user-id";
    private static final long IMAGE_MAX_BYTE_SIZE = 50;
    private static final String SAMPLE_FILE_URL = "https://media.s3.ap-southeast-1.amazonaws.com/bid-12345.csv";
    private static final String FILE_FORMAT = "text/csv";
    private static final String FILE_NAME = "bid-12345.csv";
    private static final String UPLOAD_FILE_URI = "/api/v1/files/upload";
    private static final String ERROR_STATUS = "ERROR";
    private static final String SUCCESS_STATUS = "SUCCESS";
    private static final String INTERNAL_SERVER_ERROR = "Failed due to an internal server error";
    private final ResourceBundleMessageSourceBean resourceBundleMessageSourceBean = new ResourceBundleMessageSourceBean();
    private ResourceBundleMessageSource resourceBundleMessageSource;
    Locale locale = LocaleContextHolder.getLocale();
    private final List<String> fileTypes = getFileTypes();
    private final int maxFileCount = 10;
    private MockMvc mockMvc;
    @Mock
    private FileManagerService fileManagerService;
    private MockMultipartFile file;
    @Mock
    private Translator translator;

    @Test
    void Should_ReturnOk_When_UploadingFileIsSuccess() throws Exception {
        String fileUpload = resourceBundleMessageSource.getMessage(SuccessResponseStatusType.FILE_UPLOAD.getCode(),
                null, locale);
        MockMultipartFile firstFile =
                new MockMultipartFile("files", "filename.pdf", "application/pdf", "some content".getBytes());
        when(translator.toLocale(SuccessResponseStatusType.FILE_UPLOAD.getCode()))
                .thenReturn(fileUpload);
        mockMvc.perform(MockMvcRequestBuilders.multipart(UPLOAD_FILE_URI)
                .file(firstFile)
                .header(X_USER_ID, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(SUCCESS_STATUS))
                .andExpect(jsonPath("$.message").value(SuccessResponseStatusType.FILE_UPLOAD.getMessage()))
                .andExpect(jsonPath("$.displayMessage").value(fileUpload));
    }

    @Test
    void Should_ReturnBadRequest_When_UploadingInvalidFileType() throws Exception {
        String errorMessage = resourceBundleMessageSource.getMessage(ErrorResponseStatusType
                        .UNSUPPORTED_FILE_FORMAT
                        .getCodeString(ErrorResponseStatusType.UNSUPPORTED_FILE_FORMAT.getCode()),
                null, locale);
        when(translator.toLocale(ErrorResponseStatusType.UNSUPPORTED_FILE_FORMAT.getCodeString(
                ErrorResponseStatusType.UNSUPPORTED_FILE_FORMAT.getCode())))
                .thenReturn(errorMessage);
        MockMultipartFile invalidFile = new MockMultipartFile("file", "test.json",
                "application/json", "1234567890-1234567890-1234567890-1234567890-1234567890".getBytes());
        mockMvc.perform(MockMvcRequestBuilders.multipart(UPLOAD_FILE_URI)
                .file(invalidFile).param("fileName", FILE_NAME)
                .header(X_USER_ID, USER_ID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.UNSUPPORTED_FILE_FORMAT.getMessage()))
                .andExpect(jsonPath("$.displayMessage").value(errorMessage));
    }

    @Test
    void Should_ReturnBadRequest_When_UploadingLargeFile() throws Exception {
        String errorMessage = resourceBundleMessageSource.getMessage(ErrorResponseStatusType
                        .EXCEEDED_FILE_SIZE
                        .getCodeString(ErrorResponseStatusType.EXCEEDED_FILE_SIZE.getCode()),
                null, locale);
        when(translator.toLocale(ErrorResponseStatusType.EXCEEDED_FILE_SIZE.getCodeString(
                ErrorResponseStatusType.EXCEEDED_FILE_SIZE.getCode())))
                .thenReturn(errorMessage);
        MockMultipartFile largeFile = new MockMultipartFile("files", FILE_NAME, FILE_FORMAT,
                "1234567890-1234567890-1234567890-1234567890-1234567890".getBytes());
        mockMvc.perform(MockMvcRequestBuilders.multipart(UPLOAD_FILE_URI)
                .file(largeFile).param("fileName", FILE_NAME)
                .header(X_USER_ID, USER_ID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.EXCEEDED_FILE_SIZE.getMessage()))
                .andExpect(jsonPath("$.displayMessage").value(errorMessage));
    }

    @Test
    void Should_ReturnInternalServerError_When_UploadingFileIsFailed() throws Exception {
        String errorMessage = resourceBundleMessageSource.getMessage(ErrorResponseStatusType
                        .INTERNAL_SERVER_ERROR
                        .getCodeString(ErrorResponseStatusType.INTERNAL_SERVER_ERROR.getCode()),
                null, locale);
        when(translator.toLocale(ErrorResponseStatusType.INTERNAL_SERVER_ERROR.getCodeString(
                ErrorResponseStatusType.INTERNAL_SERVER_ERROR.getCode())))
                .thenReturn(errorMessage);
        MockMultipartFile file =
                new MockMultipartFile("files", "filename.pdf", "application/pdf", "some content".getBytes());
        doThrow(new FileManagerException("Failed")).when(fileManagerService).uploadFile(any(MultipartFile.class), eq(FILE_NAME), eq(USER_ID));
        mockMvc.perform(MockMvcRequestBuilders.multipart(UPLOAD_FILE_URI)
                .file(file).param("fileName", FILE_NAME)
                .header(X_USER_ID, USER_ID))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(INTERNAL_SERVER_ERROR))
                .andExpect(jsonPath("$.displayMessage").value(errorMessage));
    }

    @BeforeEach
    void setUp() {
        initMocks(this);
        FileManagerController fileManagerController = new FileManagerController(fileManagerService,
                IMAGE_MAX_BYTE_SIZE, fileTypes, maxFileCount, translator);
        mockMvc = MockMvcBuilders.standaloneSetup(fileManagerController).build();
        file = new MockMultipartFile("file", "test.csv",
                FILE_FORMAT, "1234567890".getBytes());
        when(fileManagerService.uploadFile(any(MultipartFile.class), eq(FILE_NAME), eq(USER_ID))).thenReturn(mock(FileResponseDto.class));
        this.resourceBundleMessageSource = resourceBundleMessageSourceBean.messageSource();
    }

    private List<String> getFileTypes() {
        List<String> fileTypes = new ArrayList<>();
        fileTypes.add(0, "text/csv");
        fileTypes.add(1, "application/pdf");
        fileTypes.add(2, "audio/mpeg");
        fileTypes.add(3, "video/mp4");
        return fileTypes;
    }

}
