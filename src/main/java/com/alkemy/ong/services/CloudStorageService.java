package com.alkemy.ong.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
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
     * @throws IOException
     */
    String uploadFile(MultipartFile multipartFile) throws IOException;

    /**
     * Deletes a file form the external Cloud Storage
     *
     * @param fileUrl   the absolute url of the file to be deleted
     * @throws IOException
     */
    void deleteFileFromS3Bucket(String fileUrl) throws IOException;

    /**
     * Downloads a file from the cloud storage.
     *
     * @param fileUrl   the absolute url of the file.
     * @return  the stream to the file.
     * @throws IOException
     */
    InputStream downloadFile(String fileUrl) throws IOException;

}
