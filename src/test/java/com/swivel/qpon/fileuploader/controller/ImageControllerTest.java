package com.swivel.qpon.fileuploader.controller;

import com.swivel.qpon.fileuploader.domain.request.ImageRequestDto;
import com.swivel.qpon.fileuploader.enums.ErrorResponseStatusType;
import com.swivel.qpon.fileuploader.enums.SuccessResponseStatusType;
import com.swivel.qpon.fileuploader.exception.InvalidFileException;
import com.swivel.qpon.fileuploader.service.ImageService;
import com.swivel.qpon.fileuploader.configurations.FileUploaderControllerAdvice;
import com.swivel.qpon.fileuploader.configurations.ResourceBundleMessageSourceBean;
import com.swivel.qpon.fileuploader.configurations.Translator;
import com.swivel.qpon.fileuploader.exception.FileManagerException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * This class tests the {@link ImageController} class
 */
class ImageControllerTest {

    protected static final String APPLICATION_JSON = "application/json";
    private static final long IMAGE_MAX_BYTE_SIZE = 50;
    private static final String UPLOAD_IMAGE_URI = "/api/v1/images/upload";
    private static final String DELETE_IMAGE_URI = "/api/v1/images/delete";
    private static final String IMAGE_PNG = "image/png";
    private static final String ERROR_STATUS = "ERROR";
    private static final String SUCCESS_STATUS = "SUCCESS";
    private static final String IMAGE_UPLOAD_PARAM_NAME = "image";
    private static final String SAMPLE_IMAGE_URL = "https://media.s3.ap-southeast-1.amazonaws.com/iid-b94ce7c3-ed51-4a5e-97a4-59824baf286c";
    private final ResourceBundleMessageSourceBean resourceBundleMessageSourceBean = new ResourceBundleMessageSourceBean();
    Locale locale = LocaleContextHolder.getLocale();
    private ResourceBundleMessageSource resourceBundleMessageSource;
    private MockMvc mockMvc;
    @Mock
    private ImageService imageService;
    private MockMultipartFile image;
    private ImageRequestDto imageRequestDto;
    @Mock
    private Translator translator;

    /**
     * Upload image
     */

    @Test
    public void Should_ReturnOk_When_UploadingImageIsSuccess() throws Exception {
        String fileUpload = resourceBundleMessageSource.getMessage(SuccessResponseStatusType.IMAGE_UPLOAD.getCode(),
                null, locale);
        when(translator.toLocale(SuccessResponseStatusType.IMAGE_UPLOAD.getCode()))
                .thenReturn(fileUpload);
        mockMvc.perform(MockMvcRequestBuilders.multipart(UPLOAD_IMAGE_URI)
                .file(image))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(SUCCESS_STATUS))
                .andExpect(jsonPath("$.message").value(SuccessResponseStatusType.IMAGE_UPLOAD.getMessage()))
                .andExpect(jsonPath("$.data.imageByteSize").value(image.getSize()))
                .andExpect(jsonPath("$.data.contentType").value(IMAGE_PNG))
                .andExpect(jsonPath("$.data.imageUrl").value(SAMPLE_IMAGE_URL))
                .andExpect(jsonPath("$.displayMessage").value(fileUpload));
    }

    @Test
    public void Should_ReturnBadRequest_When_UploadingInvalidImageType() throws Exception {
        String errorMessage = resourceBundleMessageSource.getMessage(ErrorResponseStatusType
                        .INVALID_IMAGE_TYPE
                        .getCodeString(ErrorResponseStatusType.INVALID_IMAGE_TYPE.getCode()),
                null, locale);
        when(translator.toLocale(ErrorResponseStatusType.INVALID_IMAGE_TYPE.getCodeString(
                ErrorResponseStatusType.INVALID_IMAGE_TYPE.getCode())))
                .thenReturn(errorMessage);
        MockMultipartFile image = new MockMultipartFile(IMAGE_UPLOAD_PARAM_NAME, "test.json",
                "application/json", "1234567890-1234567890-1234567890-1234567890-1234567890".getBytes());
        mockMvc.perform(MockMvcRequestBuilders.multipart(UPLOAD_IMAGE_URI)
                .file(image))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.INVALID_IMAGE_TYPE.getMessage()))
                .andExpect(jsonPath("$.displayMessage").value(errorMessage));
    }

    @Test
    public void Should_ReturnBadRequest_When_UploadingLargeImage() throws Exception {
        String errorMessage = resourceBundleMessageSource.getMessage(ErrorResponseStatusType
                        .EXCEEDED_IMAGE_SIZE
                        .getCodeString(ErrorResponseStatusType.EXCEEDED_IMAGE_SIZE.getCode()),
                null, locale);
        when(translator.toLocale(ErrorResponseStatusType.EXCEEDED_IMAGE_SIZE.getCodeString(
                ErrorResponseStatusType.EXCEEDED_IMAGE_SIZE.getCode())))
                .thenReturn(errorMessage);
        MockMultipartFile image = new MockMultipartFile(IMAGE_UPLOAD_PARAM_NAME, "test.png", IMAGE_PNG,
                "1234567890-1234567890-1234567890-1234567890-1234567890".getBytes());
        mockMvc.perform(MockMvcRequestBuilders.multipart(UPLOAD_IMAGE_URI)
                .file(image))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.EXCEEDED_IMAGE_SIZE.getMessage()))
                .andExpect(jsonPath("$.displayMessage").value(errorMessage));
    }

    @Test
    public void Should_ReturnBadRequest_When_UploadingParamNotProvided() throws Exception {
        String errorMessage = resourceBundleMessageSource.getMessage(ErrorResponseStatusType
                        .MISSING_REQUIRED_FIELDS
                        .getCodeString(ErrorResponseStatusType.MISSING_REQUIRED_FIELDS.getCode()),
                null, locale);
        when(translator.toLocale(ErrorResponseStatusType.MISSING_REQUIRED_FIELDS.getCodeString(
                ErrorResponseStatusType.MISSING_REQUIRED_FIELDS.getCode())))
                .thenReturn(errorMessage);
        MockMultipartFile image = new MockMultipartFile("invalid", "test.json",
                "application/json", "1234567890-1234567890-1234567890-1234567890-1234567890".getBytes());
        mockMvc.perform(MockMvcRequestBuilders.multipart(UPLOAD_IMAGE_URI)
                .file(image))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.MISSING_REQUIRED_FIELDS.getMessage()))
                .andExpect(jsonPath("$.displayMessage").value(errorMessage));
    }

    @Test
    public void Should_ReturnBadRequest_When_InvalidUploadingParamProvided() throws Exception {
        String errorMessage = resourceBundleMessageSource.getMessage(ErrorResponseStatusType
                        .INVALID_UPLOAD_NAME
                        .getCodeString(ErrorResponseStatusType.INVALID_UPLOAD_NAME.getCode()),
                null, locale);
        when(translator.toLocale(ErrorResponseStatusType.INVALID_UPLOAD_NAME.getCodeString(
                ErrorResponseStatusType.INVALID_UPLOAD_NAME.getCode())))
                .thenReturn(errorMessage);
        MockMultipartFile image = new MockMultipartFile(IMAGE_UPLOAD_PARAM_NAME, "test.json",
                "application/json", "1234567890-1234567890-1234567890-1234567890-1234567890".getBytes());
        mockMvc.perform(MockMvcRequestBuilders.multipart(UPLOAD_IMAGE_URI)
                .file(image).param("uploadName", "file@123"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.INVALID_UPLOAD_NAME.getMessage()))
                .andExpect(jsonPath("$.displayMessage").value(errorMessage));
    }

    @Test
    public void Should_ReturnBadRequest_When_FileIsNotProvided() throws Exception {
        String errorMessage = resourceBundleMessageSource.getMessage(ErrorResponseStatusType
                        .MISSING_REQUIRED_FIELDS
                        .getCodeString(ErrorResponseStatusType.MISSING_REQUIRED_FIELDS.getCode()),
                null, locale);
        when(translator.toLocale(ErrorResponseStatusType.MISSING_REQUIRED_FIELDS.getCodeString(
                ErrorResponseStatusType.MISSING_REQUIRED_FIELDS.getCode())))
                .thenReturn(errorMessage);
        MockMultipartFile image = new MockMultipartFile(IMAGE_UPLOAD_PARAM_NAME, "test.json",
                "application/json", "".getBytes());
        mockMvc.perform(MockMvcRequestBuilders.multipart(UPLOAD_IMAGE_URI)
                .file(image).param("uploadName", "file@123"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.MISSING_REQUIRED_FIELDS.getMessage()))
                .andExpect(jsonPath("$.displayMessage").value(errorMessage));
    }

    @BeforeEach
    void setUp() {
        initMocks(this);
        ImageController imageController = new ImageController(IMAGE_MAX_BYTE_SIZE, imageService, translator);
        mockMvc = MockMvcBuilders.standaloneSetup(imageController).setControllerAdvice
                (new FileUploaderControllerAdvice(translator)).build();
        image = new MockMultipartFile(IMAGE_UPLOAD_PARAM_NAME, "test.png",
                IMAGE_PNG, "1234567890".getBytes());
        when(imageService.uploadImage(any(MultipartFile.class), eq(null))).thenReturn(SAMPLE_IMAGE_URL);
        imageRequestDto = new ImageRequestDto(SAMPLE_IMAGE_URL);
        this.resourceBundleMessageSource = resourceBundleMessageSourceBean.messageSource();
    }

    @AfterEach
    void tearDown() {
        // do nothing
    }

    @Test
    void Should_ReturnInternalServerError_When_UploadingImageIsFailed() throws Exception {
        String errorMessage = resourceBundleMessageSource.getMessage(ErrorResponseStatusType
                        .INTERNAL_SERVER_ERROR
                        .getCodeString(ErrorResponseStatusType.INTERNAL_SERVER_ERROR.getCode()),
                null, locale);
        when(translator.toLocale(ErrorResponseStatusType.INTERNAL_SERVER_ERROR.getCodeString(
                ErrorResponseStatusType.INTERNAL_SERVER_ERROR.getCode())))
                .thenReturn(errorMessage);
        doThrow(new FileManagerException("Failed")).when(imageService).uploadImage(any(MultipartFile.class), eq(null));
        mockMvc.perform(MockMvcRequestBuilders.multipart(UPLOAD_IMAGE_URI)
                .file(image))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.INTERNAL_SERVER_ERROR.getMessage()))
                .andExpect(jsonPath("$.displayMessage").value(errorMessage));
    }

    /**
     * Delete image
     */

    @Test
    void Should_ReturnOk_When_DeletingImageIsSuccessful() throws Exception {
        String deleteImage = resourceBundleMessageSource.getMessage(SuccessResponseStatusType.IMAGE_DELETE.getCode(),
                null, locale);
        when(translator.toLocale(SuccessResponseStatusType.IMAGE_DELETE.getCode()))
                .thenReturn(deleteImage);
        mockMvc.perform(MockMvcRequestBuilders.delete(DELETE_IMAGE_URI)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(imageRequestDto.toJson()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value(SuccessResponseStatusType.IMAGE_DELETE.getMessage()))
                .andExpect(jsonPath("$.displayMessage").value(deleteImage));
    }

    @Test
    void Should_BadRequestError_When_DeletingEmptyImageUrl() throws Exception {
        String errorMessage = resourceBundleMessageSource.getMessage(ErrorResponseStatusType
                        .MISSING_REQUIRED_FIELDS
                        .getCodeString(ErrorResponseStatusType.MISSING_REQUIRED_FIELDS.getCode()),
                null, locale);
        when(translator.toLocale(ErrorResponseStatusType.MISSING_REQUIRED_FIELDS.getCodeString(
                ErrorResponseStatusType.MISSING_REQUIRED_FIELDS.getCode())))
                .thenReturn(errorMessage);
        imageRequestDto.setImageUrl(" ");
        mockMvc.perform(MockMvcRequestBuilders.delete(DELETE_IMAGE_URI)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(imageRequestDto.toJson()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.MISSING_REQUIRED_FIELDS.getMessage()))
                .andExpect(jsonPath("$.displayMessage").value(errorMessage));
    }

    @Test
    void Should_BadRequestError_When_DeletingInvalidImageUrl() throws Exception {
        String errorMessage = resourceBundleMessageSource.getMessage(ErrorResponseStatusType
                        .INVALID_IMAGE_URL
                        .getCodeString(ErrorResponseStatusType.INVALID_IMAGE_URL.getCode()),
                null, locale);
        when(translator.toLocale(ErrorResponseStatusType.INVALID_IMAGE_URL.getCodeString(
                ErrorResponseStatusType.INVALID_IMAGE_URL.getCode())))
                .thenReturn(errorMessage);
        doThrow(new InvalidFileException("Failed")).when(imageService).deleteImage(SAMPLE_IMAGE_URL);
        mockMvc.perform(MockMvcRequestBuilders.delete(DELETE_IMAGE_URI)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(imageRequestDto.toJson()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.INVALID_IMAGE_URL.getMessage()))
                .andExpect(jsonPath("$.displayMessage").value(errorMessage));
    }

    @Test
    void Should_ReturnInternalServerError_When_DeletingImageIsFailed() throws Exception {
        String errorMessage = resourceBundleMessageSource.getMessage(ErrorResponseStatusType
                        .INTERNAL_SERVER_ERROR
                        .getCodeString(ErrorResponseStatusType.INTERNAL_SERVER_ERROR.getCode()),
                null, locale);
        when(translator.toLocale(ErrorResponseStatusType.INTERNAL_SERVER_ERROR.getCodeString(
                ErrorResponseStatusType.INTERNAL_SERVER_ERROR.getCode())))
                .thenReturn(errorMessage);
        doThrow(new FileManagerException("Failed")).when(imageService).deleteImage(SAMPLE_IMAGE_URL);
        mockMvc.perform(MockMvcRequestBuilders.delete(DELETE_IMAGE_URI)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(imageRequestDto.toJson()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(ERROR_STATUS))
                .andExpect(jsonPath("$.message").value(ErrorResponseStatusType.INTERNAL_SERVER_ERROR.getMessage()))
                .andExpect(jsonPath("$.displayMessage").value(errorMessage));
    }

}