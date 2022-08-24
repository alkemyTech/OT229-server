package com.alkemy.ong.controllers;

import com.alkemy.ong.exception.CloudStorageClientException;
import com.alkemy.ong.exception.CorruptedFileException;
import com.alkemy.ong.exception.FileNotFoundOnCloudException;
import com.alkemy.ong.services.CloudStorageService;
import com.alkemy.ong.utility.GlobalConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(GlobalConstants.Endpoints.CLOUD_STORAGE)
public class CloudStorageController {

    @Autowired
    private CloudStorageService amazonS3ServiceImpl;

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestParam(value = "file") MultipartFile file) throws CloudStorageClientException, CorruptedFileException {
        return ResponseEntity.ok(this.amazonS3ServiceImpl.uploadFile(file));
    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping
    public ResponseEntity<?> deleteFile(@RequestParam(value = "file_url") String fileUrl) throws CloudStorageClientException, FileNotFoundOnCloudException {
        this.amazonS3ServiceImpl.deleteFileFromS3Bucket(fileUrl);
        return ResponseEntity.ok().body("File successfully deleted");
    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    public ResponseEntity<?> downloadFile(@RequestParam("file_url") String fileUrl) throws CloudStorageClientException, FileNotFoundOnCloudException {
        InputStreamResource resource = new InputStreamResource(this.amazonS3ServiceImpl.downloadFile(fileUrl));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
    }

}
