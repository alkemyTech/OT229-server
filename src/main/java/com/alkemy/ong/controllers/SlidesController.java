package com.alkemy.ong.controllers;

import com.alkemy.ong.dto.DeleteEntityResponse;
import com.alkemy.ong.dto.ReducedSlideDTO;
import com.alkemy.ong.dto.SlidesEntityDTO;
import com.alkemy.ong.exception.CloudStorageClientException;
import com.alkemy.ong.exception.CorruptedFileException;
import com.alkemy.ong.exception.FileNotFoundOnCloudException;
import com.alkemy.ong.services.CloudStorageService;
import com.alkemy.ong.services.SlidesService;
import com.alkemy.ong.utility.GlobalConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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


    @GetMapping("/{id}")
    public ResponseEntity<?> detailedSlide(@PathVariable String id){
        try{
            SlidesEntityDTO slide = slidesService.findById(id);

            return ResponseEntity.ok().body(slide);
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping()
    public ResponseEntity<?> slideList(){
        List<ReducedSlideDTO> slides = slidesService.slideList();

        return ResponseEntity.ok().body(slides);
    }

    @PostMapping
    public ResponseEntity<?> createSlide(@RequestParam(value = "file",required = false)MultipartFile file, @ModelAttribute SlidesEntityDTO slidesDTO) throws IOException, CloudStorageClientException, CorruptedFileException {
        try {
            slidesDTO.setImageUrl(cloudStorageService.uploadBase64File(file));
            return ResponseEntity.status(HttpStatus.CREATED).body(this.slidesService.create(file,slidesDTO));
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/id")
    public ResponseEntity<?>deleteSlide(@PathVariable String id) throws CloudStorageClientException, FileNotFoundOnCloudException {
        try {
            SlidesEntityDTO slideDTO = this.slidesService.deleteSlide(id);
            return ResponseEntity.ok(new DeleteEntityResponse("Slide successful deleted",slideDTO));
        }catch (EntityNotFoundException e ){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<?>updateSlide(@RequestParam(value = "file", required = false) MultipartFile file, @Valid @ModelAttribute SlidesEntityDTO slide,@PathVariable String id) throws CloudStorageClientException, CorruptedFileException {
        try {
            return ResponseEntity.ok(this.slidesService.updateSlide(id,file,slide));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }



}
