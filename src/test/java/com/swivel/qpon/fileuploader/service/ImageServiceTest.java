package com.swivel.qpon.fileuploader.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.swivel.qpon.fileuploader.exception.InvalidFileException;
import com.swivel.qpon.fileuploader.exception.FileManagerException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * This class tests the {@link ImageService} class.
 */
class ImageServiceTest {

    private static final String BUCKET_NAME = "media";
    private static final String REGION = "ap-southeast-1";
    private static final String SAMPLE_IMAGE_URL = "https://media.s3.ap-southeast-1.amazonaws.com/iid-b94ce7c3-ed51-4a5e-97a4-59824baf286c";
    private static final String IMAGE_PNG = "image/png";
    private static final String IMAGE_NAME = "iid-b94ce7c3-ed51-4a5e-97a4-59824baf286c";
    private static final String IMAGE_UPLOAD_ERROR = "Uploading image to S3 bucket was filed";
    private static final String IMAGE_DELETE_ERROR = "Deleting image from S3 bucket was filed";
    private static final String INVALID_IMAGE_URL = "Invalid image url";
    private ImageService imageService;
    @Mock
    private AmazonS3 s3Client;
    private MockMultipartFile image;

    @BeforeEach
    void setUp() throws MalformedURLException {
        initMocks(this);
        imageService = new ImageService(s3Client, BUCKET_NAME, REGION);
        image = new MockMultipartFile("IMAGE_UPLOAD_PARAM_NAME", "test.png",
                IMAGE_PNG, "1234567890".getBytes());
        URL url = new URL(SAMPLE_IMAGE_URL);
        when(s3Client.getUrl(eq(BUCKET_NAME), anyString())).thenReturn(url);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void Should_UploadImage_When_UploadNameIsEmpty() {
        String imageUrl = imageService.uploadImage(image, "");
        verify(s3Client).putObject(eq(BUCKET_NAME), anyString(), any(), any(ObjectMetadata.class));
        verify(s3Client).getUrl(eq(BUCKET_NAME), anyString());
        assertEquals(SAMPLE_IMAGE_URL, imageUrl);
    }

    @Test
    void Should_UploadImage_When_UploadNameIsNull() {
        String imageUrl = imageService.uploadImage(image, null);
        verify(s3Client).putObject(eq(BUCKET_NAME), anyString(), any(), any(ObjectMetadata.class));
        verify(s3Client).getUrl(eq(BUCKET_NAME), anyString());
        assertEquals(SAMPLE_IMAGE_URL, imageUrl);
    }

    @Test
    void Should_UploadImage_When_UploadNameIsGiven() {
        String imageUrl = imageService.uploadImage(image, "upload-name");
        verify(s3Client).putObject(eq(BUCKET_NAME), eq("upload-name"), any(), any(ObjectMetadata.class));
        verify(s3Client).getUrl(BUCKET_NAME, "upload-name");
        assertEquals(SAMPLE_IMAGE_URL, imageUrl);
    }

    @Test
    void Should_ThrowException_When_UploadingImageWasFailedDueToAwsClientError() {
        when(s3Client.putObject(eq(BUCKET_NAME), anyString(), any(), any(ObjectMetadata.class)))
                .thenThrow(new AmazonClientException("failed"));
        FileManagerException exception = assertThrows(
                FileManagerException.class, () -> imageService.uploadImage(image, null));
        assertEquals(IMAGE_UPLOAD_ERROR, exception.getMessage());
    }

    @Test
    void Should_ThrowException_When_UploadingImageWasFailedDueToIOError() throws IOException {
        MultipartFile image = mock(MultipartFile.class);
        when(image.getInputStream()).thenThrow(new IOException("failed"));
        FileManagerException exception = assertThrows(
                FileManagerException.class, () -> imageService.uploadImage(image, null));
        assertEquals(IMAGE_UPLOAD_ERROR, exception.getMessage());
    }

    @Test
    void Should_DeleteImage() {
        imageService.deleteImage(SAMPLE_IMAGE_URL);
        verify(s3Client).deleteObject(BUCKET_NAME, IMAGE_NAME);
    }

    @Test
    void Should_ThrowException_When_DeletingImageWasFailedDueToAwsClientError() {
        doThrow(new AmazonClientException("failed")).when(s3Client).deleteObject(BUCKET_NAME, IMAGE_NAME);
        FileManagerException exception = assertThrows(
                FileManagerException.class, () -> imageService.deleteImage(SAMPLE_IMAGE_URL));
        assertEquals(IMAGE_DELETE_ERROR, exception.getMessage());
    }

    @Test
    void Should_ThrowException_When_DeletingInvalidImageUrl() {
        InvalidFileException exception = assertThrows(
                InvalidFileException.class, () -> imageService.deleteImage("https://image"));
        assertEquals(INVALID_IMAGE_URL, exception.getMessage());
    }

}