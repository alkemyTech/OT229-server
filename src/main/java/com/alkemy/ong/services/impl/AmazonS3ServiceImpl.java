package com.alkemy.ong.services.impl;

import com.alkemy.ong.configuration.AmazonS3CredentialsConfiguration;
import com.alkemy.ong.exception.AmazonS3Exception;
import com.alkemy.ong.services.CloudStorageService;
import com.alkemy.ong.utility.FileManager;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@Service
public class AmazonS3ServiceImpl implements CloudStorageService {

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
        Regions region = Regions.valueOf( this.credentialsConfiguration.getRegion() );
        this.s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(credentialsProvider)
                .withRegion(region)
                .build();
    }

    /**
     * Uploads a file to the Amazon S3 Bucket
     *
     * @param multipartFile the file to be uploaded
     * @return  the absolute url to access the uploaded file
     * @throws IOException if it fails to convert the multi-part file into a file, or if the original file name can't be
     *                      retrieved.
     * @throws AmazonS3Exception if there was a problem with the Amazon S3 client.
     */
    @Override
    public String uploadFile(MultipartFile multipartFile) throws IOException, AmazonS3Exception {

        File file = FileManager.convertMultiPartToFile(multipartFile);
        String fileName = FileManager.buildFileName(multipartFile).withTimeStamp().withoutSpaces().build();
        String bucketName = this.credentialsConfiguration.getBucketName();
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, file);
        try {
            s3client.putObject(putObjectRequest);
        } catch (Exception e) {
            throw new AmazonS3Exception(e.getMessage(), e);
        } finally {
            file.delete();
        }
        return this.s3client.getUrl(bucketName, fileName).toString();
    }

    /**
     * Deletes a file from the S3 Bucket
     *
     * @param fileUrl   the absolute url of the file to be deleted
     * @throws AmazonS3Exception    if there was an issue with the S3 client or server
     * @throws FileNotFoundException    if there was no file stored with the specified url
     */
    @Override
    public void deleteFileFromS3Bucket(String fileUrl) throws AmazonS3Exception, FileNotFoundException {

        String fileName = this.retrieveFileNameFromUrl(fileUrl);
        String bucketName = this.credentialsConfiguration.getBucketName();
        if (!this.s3client.doesObjectExist(bucketName, fileName)) {
            throw new FileNotFoundException("File " + fileName + " not found");
        }
        try {
            s3client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
        } catch (Exception e) {
            throw new AmazonS3Exception(e.getMessage(), e);
        }
    }

    /**
     * Downloads a file from the S3 storage
     *
     * This method doesn't really download the file, it generates a stream to the file source to be downloaded by
     * the client. If returned by a Spring controller, it should be encapsulated inside an InputStreamResource object.
     *
     * @param fileUrl   the absolute url of the file.
     * @return  an input stream to the file.
     * @throws AmazonS3Exception    if there was an error with the S3 service.
     * @throws FileNotFoundException    if there was no file stored with the specified url
     * @see org.springframework.core.io.InputStreamResource
     */
    @Override
    public S3ObjectInputStream downloadFile(String fileUrl) throws AmazonS3Exception, FileNotFoundException {
        String fileName = this.retrieveFileNameFromUrl(fileUrl);
        String bucketName = this.credentialsConfiguration.getBucketName();
        if (!this.s3client.doesObjectExist(bucketName, fileName)) {
            throw new FileNotFoundException("File " + fileName + " not found");
        }
        try {
            S3Object s3Object = this.s3client.getObject(bucketName, fileName);
            return s3Object.getObjectContent();
        } catch (Exception e) {
            throw new AmazonS3Exception(e.getMessage(), e);
        }
    }

    /**
     * Retrieves the file name alone from the absolute url to access it in the cloud.
     *
     * @param fileUrl   the absolute url of the stored file
     * @return  the name of the file
     */
    private String retrieveFileNameFromUrl(String fileUrl) {
        return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
    }

}