package com.alkemy.ong.controllers;

import com.alkemy.ong.dto.TestimonialDTORequest;
import com.alkemy.ong.dto.TestimonialDTOResponse;
import com.alkemy.ong.exception.CloudStorageClientException;
import com.alkemy.ong.exception.CorruptedFileException;
import com.alkemy.ong.exception.FileNotFoundOnCloudException;
import com.alkemy.ong.services.TestimonialService;
import com.alkemy.ong.utility.GlobalConstants;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Create a new testimonial")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201",description = "Testimonial successfully created",
        content = {
            @Content(mediaType = "application/json", schema=@Schema(implementation =  TestimonialDTOResponse.class))
        }),
            @ApiResponse(responseCode="404", description = "Testimonial could not be created",
                content = {
                @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))
            })
    })
    @PostMapping
    public ResponseEntity<?> createTestimonial(@Valid @ModelAttribute TestimonialDTORequest request,
                                               @RequestParam("file") MultipartFile file) throws CloudStorageClientException, CorruptedFileException {
        return new ResponseEntity<>(service.create(file,request), HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing testimonial")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Testimonial successfully updated",
            content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = TestimonialDTOResponse.class))
            }),
        @ApiResponse(responseCode = "404", description = "Testimonial not found",
            content = {
            @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))
        }),
    })
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

    @Operation(summary = "Delete a testimonial")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Testimonial successfully deleted",
            content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = TestimonialDTOResponse.class))
            }),
        @ApiResponse(responseCode = "404", description = "Testimonial not found",
            content = {
            @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))
        }),
    })
    @DeleteMapping
    public ResponseEntity<?>deleteTestimonial(@RequestParam("id")String id)throws CloudStorageClientException, FileNotFoundOnCloudException {

        try{
            return new ResponseEntity<>(service.delete(id),HttpStatus.NO_CONTENT);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
