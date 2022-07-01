package com.alkemy.ong.controllers;

import com.alkemy.ong.dto.ActivityDTO;
import com.alkemy.ong.exception.AmazonS3Exception;
import com.alkemy.ong.services.ActivitiesService;
import com.alkemy.ong.services.CloudStorageService;
import com.alkemy.ong.utility.GlobalConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping(GlobalConstants.Endpoints.ACTIVITIES)
public class ActivitiesController {

    @Autowired
    private ActivitiesService activitiesService;

    @Autowired
    private CloudStorageService amazonS3Service;

    @PostMapping
    public ResponseEntity<?> save(@RequestParam(value = "file", required = false) MultipartFile file,
                                   @Valid @ModelAttribute ActivityDTO dto) {

        try {
            if (file != null) {
                dto.setImage(amazonS3Service.uploadFile(file));
            } else {
                dto.setImage(null);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(activitiesService.save(dto));

        } catch (AmazonS3Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getMessage());

        } catch (IOException e) {

            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
