package com.alkemy.ong.controllers;

import com.alkemy.ong.exception.AmazonS3Exception;
import com.alkemy.ong.services.CloudStorageService;
import com.alkemy.ong.services.impl.AmazonS3ServiceImpl;
import com.alkemy.ong.utility.GlobalConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@RequestMapping(GlobalConstants.Endpoints.CLOUD_STORAGE)
public class CloudStorageController {

    @Autowired
    private CloudStorageService amazonS3ServiceImpl;

    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestParam(value = "file") MultipartFile file) {
        try {
            return ResponseEntity.ok(this.amazonS3ServiceImpl.uploadFile(file));
        } catch (AmazonS3Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteFile(@RequestPart(value = "file_url") String fileUrl) {
        try{
            this.amazonS3ServiceImpl.deleteFileFromS3Bucket(fileUrl);
            return ResponseEntity.ok().body("File successfully deleted");
        } catch (AmazonS3Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getMessage());
        } catch (FileNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> downloadFile(@RequestParam("file_url") String fileUrl) {
        try {
            InputStreamResource resource = new InputStreamResource(this.amazonS3ServiceImpl.downloadFile(fileUrl));
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
        } catch (FileNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (AmazonS3Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
