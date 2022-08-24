package com.alkemy.ong.controllers;

import com.alkemy.ong.dto.DeleteEntityResponse;
import com.alkemy.ong.dto.ReducedSlideDTO;
import com.alkemy.ong.dto.SlidesEntityDTO;
import com.alkemy.ong.dto.SlidesEntityDTORequest;
import com.alkemy.ong.exception.CloudStorageClientException;
import com.alkemy.ong.exception.CorruptedFileException;
import com.alkemy.ong.exception.FileNotFoundOnCloudException;
import com.alkemy.ong.services.CloudStorageService;
import com.alkemy.ong.services.SlidesService;
import com.alkemy.ong.utility.GlobalConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(GlobalConstants.Endpoints.SLIDES)
public class SlidesController {

    @Autowired
    SlidesService slidesService;
    @Autowired
    private CloudStorageService cloudStorageService;


    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{id}")
    public ResponseEntity<?> detailedSlide(@PathVariable String id){
        try{
            SlidesEntityDTO slide = slidesService.findById(id);

            return ResponseEntity.ok().body(slide);
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping()
    public ResponseEntity<?> slideList(){
        List<ReducedSlideDTO> slides = slidesService.slideList();

        return ResponseEntity.ok().body(slides);
    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<?> createSlide(@Valid @RequestBody SlidesEntityDTORequest slidesDTO) throws IOException, CloudStorageClientException, CorruptedFileException {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(this.slidesService.create(slidesDTO));
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/id")
    public ResponseEntity<?>deleteSlide(@PathVariable String id) throws CloudStorageClientException, FileNotFoundOnCloudException {
        try {
            SlidesEntityDTO slideDTO = this.slidesService.deleteSlide(id);
            return ResponseEntity.ok(new DeleteEntityResponse("Slide successful deleted",slideDTO));
        }catch (EntityNotFoundException e ){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}")
    public ResponseEntity<?>updateSlide(@Valid @RequestBody SlidesEntityDTORequest slide,@PathVariable String id) throws CloudStorageClientException, CorruptedFileException {
        try {
            return ResponseEntity.ok(this.slidesService.updateSlide(id,slide));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
