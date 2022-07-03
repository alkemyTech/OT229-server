package com.alkemy.ong.controllers;

import com.alkemy.ong.dto.ActivityDTO;
import com.alkemy.ong.exception.ActivityNamePresentException;
import com.alkemy.ong.exception.ActivityNotFoundException;
import com.alkemy.ong.exception.AmazonS3Exception;
import com.alkemy.ong.services.ActivitiesService;
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

    @PostMapping
    public ResponseEntity<?> save(@RequestParam(value = "file", required = false) MultipartFile file,
                                   @Valid @ModelAttribute ActivityDTO dto) {

        try {

            return ResponseEntity.status(HttpStatus.CREATED).body(activitiesService.save(file,dto));

        } catch (ActivityNamePresentException e) {

            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());

        } catch (AmazonS3Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getMessage());
        }
        catch (IOException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> edit (@RequestParam(value = "file", required = false) MultipartFile file,
                                   @Valid @ModelAttribute ActivityDTO dto, @PathVariable String id) {

        try {

            return ResponseEntity.status(HttpStatus.OK).body(activitiesService.edit(file,dto,id));

        } catch (ActivityNotFoundException e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());


        } catch (ActivityNamePresentException e) {

            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());

        } catch (AmazonS3Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getMessage());
        }
        catch (IOException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }


    }

}