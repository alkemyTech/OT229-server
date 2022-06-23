package com.alkemy.ong.services;

import com.alkemy.ong.configuration.AmazonS3CredentialsConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class AmazonS3Service implements CloudStorageService {

    @Autowired
    private AmazonS3CredentialsConfiguration credentialsConfiguration;

    /**
     * Object from the AWS library which serves as the core of the functionality for this class.
     */
    private AmazonS3 s3client;

    /**
     * Initializes the Amazon S3 client.
     *
     * Since the credentials dependency needs to be injected by Spring, and the Amazon S3 client builder needs it to
     * create the client instance which is the core of this class, this method is called automatically after the service
     * is instantiated and its dependencies are injected (due to the PostConstruct annotation) to generate the client
     * so that the service is ready to be used.
     * (Alternately an explicit constructor for the class could be defined with this method's content inside)
     */
    @PostConstruct
    private void initializeS3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(
                credentialsConfiguration.getAccessKey(),
                credentialsConfiguration.getSecretKey()
        );
        AWSStaticCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);
        this.s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(credentialsProvider)
                .withRegion(Regions.US_EAST_1)
                .build();
    }

}
