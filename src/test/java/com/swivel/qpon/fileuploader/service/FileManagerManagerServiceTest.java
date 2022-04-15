package com.swivel.qpon.fileuploader.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.S3Object;
import com.swivel.qpon.fileuploader.exception.FileManagerException;
import com.swivel.qpon.fileuploader.repository.FileManagerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * This class tests the {@link FileManagerManagerServiceTest} class.
 */
class FileManagerManagerServiceTest {

    private static final String BUCKET_NAME = "media";
    private static final String REGION = "ap-southeast-1";
    private static final String SAMPLE_FILE_URL = "https://media.s3.ap-southeast-1.amazonaws.com/bid-12345.csv";
    private static final String FILE_FORMAT = "text/csv";
    private static final String FILE_NAME = "bid-12345.csv";
    private static final String FILE_UPLOAD_ERROR = "Uploading file to S3 bucket was filed";
    private static final String FILE_DOWNLOAD_ERROR = "Error occurred while downloading the file";
    private static final String FILE_NOT_FOUND = "File does not exists in s3";
    private static final String IMAGE_DELETE_ERROR = "Deleting file from S3 bucket was filed";
    private static final String INVALID_IMAGE_URL = "Invalid image url";
    private static final String USER_ID = "uid-123";
    private FileManagerService fileManagerService;

    @Mock
    private FileManagerRepository fileManagerRepository;
    @Mock
    private AmazonS3 s3Client;
    private MockMultipartFile file;
    private final long maxByteSize = 10000000;

    @BeforeEach
    void setUp() throws MalformedURLException {
        initMocks(this);
        fileManagerService = new FileManagerService(s3Client, BUCKET_NAME, fileManagerRepository, maxByteSize);
        file = new MockMultipartFile(FILE_NAME, FILE_NAME,
                FILE_FORMAT, "1234567890".getBytes());
        URL url = new URL(SAMPLE_FILE_URL);
        when(s3Client.getUrl(eq(BUCKET_NAME), anyString())).thenReturn(url);
    }

//    @Test
//    void Should_UploadFile_When_UploadNameIsGiven() throws IOException {
//        FileResponseDto fileResponseDto = fileManagerService.uploadFile(file, FILE_NAME, USER_ID);
//        verify(s3Client).putObject(BUCKET_NAME, FILE_NAME, file.getInputStream(), any(ObjectMetadata.class));
//        verify(s3Client).getUrl(BUCKET_NAME, FILE_NAME);
//        assertEquals(SAMPLE_FILE_URL, fileResponseDto.getUrl());
//    }

    @Test
    void Should_ThrowException_When_UploadingImageWasFailedDueToIOError() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenThrow(new IOException("failed"));
        FileManagerException exception = assertThrows(
                FileManagerException.class, () -> fileManagerService.uploadFile(file, null, USER_ID));
        assertEquals(FILE_UPLOAD_ERROR, exception.getMessage());
    }

    @Test
    void Should_DownloadFile_When_FileNameIsGiven() throws Exception {
        S3Object s3Object = new S3Object();
        String s = "12345678";
        InputStream inputStream = new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
        s3Object.setObjectContent(inputStream);
        when(s3Client.getObject(BUCKET_NAME, FILE_NAME)).thenReturn(s3Object);
        byte[] bytes = fileManagerService.downloadFile(FILE_NAME);
        verify(s3Client).getObject(BUCKET_NAME, FILE_NAME);
        assertNotNull(bytes);
    }

    @Test
    void Should_ThrowException_When_FileNotFound() {
        AmazonS3Exception amazonS3Exception = new AmazonS3Exception("failed");
        amazonS3Exception.setStatusCode(404);
        when(s3Client.getObject(eq(BUCKET_NAME), eq(FILE_NAME))).thenThrow(amazonS3Exception);
        NoSuchFileException exception = assertThrows(
                NoSuchFileException.class, () -> fileManagerService.downloadFile(FILE_NAME));
        assertEquals(FILE_NOT_FOUND, exception.getMessage());
    }

    @Test
    void Should_ThrowException_When_FileNotFoundOrErrorWhileDownloading() {
        when(s3Client.getObject(eq(BUCKET_NAME), eq(FILE_NAME))).thenThrow(new AmazonS3Exception("failed"));
        FileManagerException exception = assertThrows(
                FileManagerException.class, () -> fileManagerService.downloadFile(FILE_NAME));
        assertEquals(FILE_DOWNLOAD_ERROR, exception.getMessage());
    }

    @Test
    void Should_ThrowException_When_FailedToDownload() {
        when(s3Client.getObject(eq(BUCKET_NAME), eq(FILE_NAME))).thenThrow(new AmazonClientException("failed"));
        FileManagerException exception = assertThrows(
                FileManagerException.class, () -> fileManagerService.downloadFile(FILE_NAME));
        assertEquals(FILE_DOWNLOAD_ERROR, exception.getMessage());
    }
}
