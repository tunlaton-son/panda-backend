package com.backend.aiblog.config.security.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonS3Config {
    @Value("${amazonS3.accessKey}")
    private String accessKey;

    @Value("${amazonS3.secretAccessKey}")
    private String secretAccessKey;

    @Value("${amazonS3.region}")
    private String region;

    @Bean
    public AmazonS3 amazonS3Client() {
        return AmazonS3Client.builder()
                .withRegion(region)
                .withCredentials(
                        new AWSStaticCredentialsProvider(
                                new BasicAWSCredentials(accessKey, secretAccessKey)))
                .build();
    }

}

