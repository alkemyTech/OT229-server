package com.alkemy.ong.controllers;

import com.alkemy.ong.dto.TestimonialDTORequest;
import com.alkemy.ong.exception.CloudStorageClientException;
import com.alkemy.ong.exception.CorruptedFileException;
import com.alkemy.ong.exception.FileNotFoundOnCloudException;
import com.alkemy.ong.exception.PageIndexOutOfBoundsException;
import com.alkemy.ong.services.TestimonialService;
import com.alkemy.ong.utility.GlobalConstants;
import javassist.NotFoundException;
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
    @PutMapping
    public ResponseEntity<?> updateTestimonial(@Valid @ModelAttribute TestimonialDTORequest request,
                                               @RequestParam(value = "file", required = false) MultipartFile file,
                                               @RequestParam("id")String id) throws CloudStorageClientException, CorruptedFileException {
       try{
           return new ResponseEntity<>(service.update(id,file,request), HttpStatus.OK);
       } catch (NotFoundException e) {
         return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
       }

    }
    @DeleteMapping
    public ResponseEntity<?>deleteTestimonial(@RequestParam("id")String id)throws CloudStorageClientException, FileNotFoundOnCloudException {

        try{
            return new ResponseEntity<>(service.delete(id),HttpStatus.NO_CONTENT);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllTestimonies(@RequestParam (value = GlobalConstants.PAGE_INDEX_PARAM) int pageNumber) throws PageIndexOutOfBoundsException {
        return ResponseEntity.ok(service.getAllTestimonies(pageNumber));
    }
}
