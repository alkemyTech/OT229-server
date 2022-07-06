package com.alkemy.ong.controllers;

import com.alkemy.ong.dto.TestimonialDTORequest;
import com.alkemy.ong.exception.CloudStorageClientException;
import com.alkemy.ong.exception.CorruptedFileException;
import com.alkemy.ong.services.TestimonialService;
import com.alkemy.ong.utility.GlobalConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping(GlobalConstants.Endpoints.TESTIMONIALS)
public class TestimonialController {

    @Autowired
    private TestimonialService service;

    @PostMapping
    public ResponseEntity<?> createTestimonial(@Valid @ModelAttribute TestimonialDTORequest request,
                                               @RequestParam("file") MultipartFile file) throws CloudStorageClientException, CorruptedFileException {
        return new ResponseEntity<>(service.create(file,request), HttpStatus.CREATED);
    }

}
