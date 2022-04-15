package com.swivel.qpon.fileuploader.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.swivel.qpon.fileuploader.exception.InvalidFileException;
import com.swivel.qpon.fileuploader.exception.FileManagerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;

/**
 * Image Service
 */
@Service
public class ImageService {

    private static final String IMAGE_NAME_PREFIX = "iid-";
    private static final String IMAGE_URL_PREFIX_FORMAT = "https://##BUCKET_NAME##.s3.##REGION##.amazonaws.com/";
    private static final String BUCKET_NAME_KEY = "##BUCKET_NAME##";
    private static final String REGION_KEY = "##REGION##";
    private static final String EMPTY_STRING = "";
    private static final String IMAGE_UPLOAD_ERROR = "Uploading image to S3 bucket was filed";
    private static final String IMAGE_DELETE_ERROR = "Deleting image from S3 bucket was filed";
    private static final String INVALID_IMAGE_URL = "Invalid image url";
    private final AmazonS3 s3Client;
    private final String bucketName;
    private final String imageUrlPrefix;

    @Autowired
    public ImageService(AmazonS3 s3Client, @Value("${aws.bucket.name}") String bucketName,
                        @Value("${aws.region}") String region) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.imageUrlPrefix = IMAGE_URL_PREFIX_FORMAT.replace(BUCKET_NAME_KEY, bucketName).replace(REGION_KEY, region);
    }

    /**
     * This method generates unique image name.
     *
     * @return unique image name
     */
    private static String generateUniqueName() {
        return IMAGE_NAME_PREFIX + UUID.randomUUID();
    }

    /**
     * This method stores the given image in s3 bucket and returns public access url.
     * Upload name is used as image name when upload name is given, else new name is used.
     *
     * @param image image
     * @return public access url
     */
    public String uploadImage(MultipartFile image, String uploadName) {
        String imageName = (uploadName != null && !uploadName.trim().isEmpty()) ? uploadName : generateUniqueName();
        try {
            s3Client.putObject(bucketName, imageName, image.getInputStream(), getMetaData(image));
            URL imageUrl = s3Client.getUrl(bucketName, imageName);
            return imageUrl == null ? null : imageUrl.toString();
        } catch (AmazonClientException | IOException e) {
            throw new FileManagerException(IMAGE_UPLOAD_ERROR, e);
        }
    }

    /**
     * This method deletes the  image by imageUrl.
     *
     * @param imageUrl imageUrl
     */
    public void deleteImage(String imageUrl) {
        try {
            String imageName = getImageName(imageUrl);
            s3Client.deleteObject(bucketName, imageName);
        } catch (AmazonClientException e) {
            throw new FileManagerException(IMAGE_DELETE_ERROR, e);
        }
    }

    /**
     * This method returns image name from the url.
     *
     * @param imageUrl imageUrl
     * @return image name
     */
    private String getImageName(String imageUrl) {
        if (imageUrl.startsWith(imageUrlPrefix) && !imageUrl.replace(imageUrlPrefix, EMPTY_STRING).isEmpty()) {
            return imageUrl.replace(imageUrlPrefix, EMPTY_STRING).trim();
        } else {
            throw new InvalidFileException(INVALID_IMAGE_URL);
        }
    }

    /**
     * This method generates meta data for the given image.
     *
     * @param image image
     * @return metadata
     */
    private ObjectMetadata getMetaData(MultipartFile image) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(image.getContentType());
        objectMetadata.setContentLength(image.getSize());
        return objectMetadata;
    }

}
