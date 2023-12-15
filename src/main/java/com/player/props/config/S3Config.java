package com.player.props.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import org.springframework.beans.factory.annotation.Value;

@Configuration
public class S3Config {

    @Value("${application.bucket.name}")
    private String bucketName;

    @Value("${aws.region.static}")
    private String region;

    private String accessKey = System.getenv("aws_access_key");

    private String secretAccessKey = System.getenv("aws_secret_access_key");

    @Bean
    public AmazonS3 S3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretAccessKey);

        return AmazonS3ClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withRegion(region)
            .build();
    }
    
}
