package com.alkemy.ong.controllers;

import com.alkemy.ong.dto.DefaultErrorResponseDto;
import com.alkemy.ong.dto.TestimonialDTORequest;
import com.alkemy.ong.dto.TestimonialDTOResponse;
import com.alkemy.ong.dto.TestimonialListResponse;
import com.alkemy.ong.exception.CloudStorageClientException;
import com.alkemy.ong.exception.CorruptedFileException;
import com.alkemy.ong.exception.FileNotFoundOnCloudException;
import com.alkemy.ong.exception.PageIndexOutOfBoundsException;
import com.alkemy.ong.security.payload.ValidationErrorResponse;
import com.alkemy.ong.services.TestimonialService;
import com.alkemy.ong.utility.GlobalConstants;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

    @Operation(summary = "Create a new testimonial", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Testimonial successfully created",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = TestimonialDTOResponse.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Corrupted image file or attribute missing or not valid",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(oneOf = {ValidationErrorResponse.class, String.class}))
                    }),
            @ApiResponse(responseCode = "403", description = "Forbidden: you're not authorized to perform the current action",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "502", description = "Bad Gateway: There was a problem with the cloud storage service",
                    content = {
                            @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "There was a problem with the cloud storage service. Problem is: ..."))
                    })
    })
    @PostMapping
    public ResponseEntity<?> createTestimonial(@Valid @RequestBody TestimonialDTORequest request) throws CloudStorageClientException, CorruptedFileException {
        return new ResponseEntity<>(service.create(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing testimonial", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Testimonial successfully updated",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = TestimonialDTOResponse.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Image file corrupted or attribute/param missing or not valid",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(oneOf = {ValidationErrorResponse.class, String.class}))
                    }),
            @ApiResponse(responseCode = "403", description = "Forbidden: you're not authorized to perform the current action",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Testimonial not found",
                    content = {
                            @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Testimonial not found."))
                    }),
            @ApiResponse(responseCode = "502", description = "Bad Gateway: There was a problem with the cloud storage service",
                    content = {
                            @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "There was a problem with the cloud storage service. Problem is: ..."))
                    })
    })
    @PutMapping
    public ResponseEntity<?> updateTestimonial(@Valid @RequestBody TestimonialDTORequest request,
                                               @Parameter(name = "id", description = "The id of the testimonial to be updated", example = "3b6f64ed-ecaa-4ae1-9e97-091464bc8dc1")
                                               @RequestParam("id") String id) throws CloudStorageClientException, CorruptedFileException {
        try {
            return new ResponseEntity<>(service.update(id, request), HttpStatus.OK);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

    }

    @Operation(summary = "Delete a testimonial", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Testimonial successfully deleted",
                    content = {
                            @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Successfully deleted testimonial with id 3b6f64ed-ecaa-4ae1-9e97-091464bc8dc1"))
                    }),
            @ApiResponse(responseCode = "400", description = "Id param missing",
                    content = {
                            @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Required param id missing."))
                    }),
            @ApiResponse(responseCode = "403", description = "Forbidden: you're not authorized to perform the current action",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultErrorResponseDto.class))
                    }),
            @ApiResponse(responseCode = "404", description = "Testimonial id not found or associated image not found on server",
                    content = {
                            @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Testimonial not found."))
                    }),
            @ApiResponse(responseCode = "502", description = "Bad Gateway: There was a problem with the cloud storage service",
                    content = {
                            @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "There was a problem with the cloud storage service. Problem is: ..."))
                    })
    })
    @DeleteMapping
    public ResponseEntity<?> deleteTestimonial(
            @Parameter(name = "id", description = "The id of the testimonial to be deleted", example = "3b6f64ed-ecaa-4ae1-9e97-091464bc8dc1")
            @RequestParam("id") String id) throws CloudStorageClientException, FileNotFoundOnCloudException {

        try {
            return new ResponseEntity<>(service.delete(id), HttpStatus.OK);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Get all testimonials, paginated", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Result list successfully returned",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = TestimonialListResponse.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Page number not valid or missing",
                    content = {
                            @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "The Page number must be 0 or positive."))
                    }),
            @ApiResponse(responseCode = "403", description = "Forbidden: you're not authorized to perform the current action",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultErrorResponseDto.class))
                    })
    })
    @GetMapping
    public ResponseEntity<?> getAllTestimonies(
            @Parameter(name = "page", description = "The page number of results to fetch", example = "2")
            @RequestParam(value = GlobalConstants.PAGE_INDEX_PARAM) int pageNumber) throws PageIndexOutOfBoundsException {

        return ResponseEntity.ok(service.getAllTestimonies(pageNumber));
    }

}
