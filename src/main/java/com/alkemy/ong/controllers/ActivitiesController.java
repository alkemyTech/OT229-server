package com.alkemy.ong.controllers;

import com.alkemy.ong.dto.ActivityDTORequest;
import com.alkemy.ong.exception.ActivityNamePresentException;
import com.alkemy.ong.exception.ActivityNotFoundException;
import com.alkemy.ong.exception.CloudStorageClientException;
import com.alkemy.ong.exception.CorruptedFileException;
import com.alkemy.ong.services.ActivitiesService;
import com.alkemy.ong.utility.GlobalConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping(GlobalConstants.Endpoints.ACTIVITIES)
public class ActivitiesController {

    @Autowired
    private ActivitiesService activitiesService;

    @PostMapping
    public ResponseEntity<?> save(@Valid @RequestBody ActivityDTORequest dto) throws CloudStorageClientException, CorruptedFileException {

        try {

            return ResponseEntity.status(HttpStatus.CREATED).body(activitiesService.save(dto));

        } catch (ActivityNamePresentException e) {

            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());

        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> edit (@Valid @RequestBody ActivityDTORequest dto, @PathVariable String id) throws CloudStorageClientException, CorruptedFileException {

        try {

            return ResponseEntity.status(HttpStatus.OK).body(activitiesService.edit(dto,id));

        } catch (ActivityNotFoundException e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());


        } catch (ActivityNamePresentException e) {

            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());

        }


    }

}
