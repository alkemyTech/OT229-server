package com.alkemy.ong.controllers;

import com.alkemy.ong.dto.ReducedSlideDTO;
import com.alkemy.ong.dto.SlidesEntityDTO;
import com.alkemy.ong.exception.CloudStorageClientException;
import com.alkemy.ong.exception.CorruptedFileException;
import com.alkemy.ong.services.CloudStorageService;
import com.alkemy.ong.services.SlidesService;
import com.alkemy.ong.utility.GlobalConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<?> createSlide(@RequestParam(value = "file",required = false)MultipartFile file, @ModelAttribute SlidesEntityDTO slidesDTO) throws CloudStorageClientException, CorruptedFileException {
        try {
            slidesDTO.setImageUrl(cloudStorageService.uploadBase64File(file));
            return ResponseEntity.status(HttpStatus.CREATED).body(this.slidesService.create(file,slidesDTO));
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
