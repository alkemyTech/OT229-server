package com.alkemy.ong.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ConfigurationProperties(prefix = "aws.s3.properties")
@Configuration
public class AmazonS3CredentialsConfiguration {

    private String endpointUrl;
    private String region;
    private String accessKey;
    private String secretKey;
    private String bucketName;

}
