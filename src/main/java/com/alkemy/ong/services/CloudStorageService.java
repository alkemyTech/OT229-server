package com.alkemy.ong.services;

import com.alkemy.ong.exception.CloudStorageClientException;
import com.alkemy.ong.exception.CorruptedFileException;
import com.alkemy.ong.exception.FileNotFoundOnCloudException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface for the services used to perform file CRUD operations against external cloud storage services.
 */
public interface CloudStorageService {

    /**
     * Uploads a file to the external Cloud Storage
     *
     * @param multipartFile the file to be uploaded
     * @return  the absolute url to access the uploaded file
     * @throws CorruptedFileException if there was a problem with the received file.
     * @throws CloudStorageClientException if there was a problem with the cloud storage client.
     */
    String uploadFile(MultipartFile multipartFile) throws CorruptedFileException, CloudStorageClientException;

    /**
     * Deletes a file form the external Cloud Storage
     *
     * @param fileUrl   the absolute url of the file to be deleted
     * @throws CloudStorageClientException    if there was an issue with the cloud storage client or server
     * @throws FileNotFoundOnCloudException    if there was no file stored with the specified url
     */
    void deleteFileFromS3Bucket(String fileUrl) throws CloudStorageClientException, FileNotFoundOnCloudException;

    /**
     * Downloads a file from the cloud storage.
     *
     * @param fileUrl   the absolute url of the file.
     * @return  the stream to the file.
     * @throws CloudStorageClientException    if there was an error with the cloud storage client or server.
     * @throws FileNotFoundOnCloudException    if there was no file stored with the specified url
     */
    InputStream downloadFile(String fileUrl) throws CloudStorageClientException, FileNotFoundOnCloudException;

    /**
     * Uploads a file to the external Cloud Storage.
     *
     * The file should be encoded in Base64. It will then be decoded and uploaded to the cloud.
     *
     * @param multipartFile the file to be uploaded, decoded in Base64.
     * @return  the absolute url to access the uploaded file.
     * @throws CorruptedFileException if there was a problem with the received file.
     * @throws CloudStorageClientException if there was a problem with the Amazon S3 client.
     */
    String uploadBase64File(MultipartFile multipartFile) throws CorruptedFileException, CloudStorageClientException;

    /**
     * Uploads a file to the external Cloud Storage.
     *
     * The file should be encoded in Base64. It will then be decoded and uploaded to the cloud.
     *
     * @param encodedImage the encoded file in the resulting string format.
     * @param fileName the original file name, extension included.
     * @return  the absolute url to access the uploaded file.
     * @throws CorruptedFileException if there was a problem with the received file.
     * @throws CloudStorageClientException if there was a problem with the Amazon S3 client.
     */
    String uploadBase64File(String encodedImage, String fileName) throws CorruptedFileException, CloudStorageClientException;
}
