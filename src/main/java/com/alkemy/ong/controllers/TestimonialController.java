package com.alkemy.ong.controllers;

import com.alkemy.ong.dto.TestimonialDTORequest;
import com.alkemy.ong.exception.CloudStorageClientException;
import com.alkemy.ong.exception.CorruptedFileException;
import com.alkemy.ong.exception.FileNotFoundOnCloudException;
import com.alkemy.ong.services.TestimonialService;
import com.alkemy.ong.utility.GlobalConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(GlobalConstants.Endpoints.TESTIMONIALS)
public class TestimonialController {

    @Autowired
    private TestimonialService service;

    @Operation(summary = "Crear un testimonial", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<?> createTestimonial(@Valid @RequestBody TestimonialDTORequest request) throws CloudStorageClientException, CorruptedFileException {

        return new ResponseEntity<>(service.create(request), HttpStatus.CREATED);

    }

    @Operation(summary = "Actualizar un testimonial", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping
    public ResponseEntity<?> updateTestimonial(@Valid @RequestBody TestimonialDTORequest request,
                                               @RequestParam("id")String id) throws CloudStorageClientException, CorruptedFileException {
       try{
           return new ResponseEntity<>(service.update(id,request), HttpStatus.OK);
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

}
