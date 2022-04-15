package com.swivel.qpon.fileuploader.configurations;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This holds aws configurations.
 */
@Configuration
public class AwsS3Config {

    @Bean
    public AmazonS3 awsS3Client() {
        return AmazonS3ClientBuilder.standard().withRegion("ap-southeast-1").build();
    }
}
