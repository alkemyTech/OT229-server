package com.alkemy.ong.controllers;

import com.alkemy.ong.dto.SlidesEntityDTO;
import com.alkemy.ong.services.SlidesService;
import com.alkemy.ong.utility.GlobalConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(GlobalConstants.Endpoints.SLIDES)
public class SlidesController {

    @Autowired
    SlidesService slidesService;


    @GetMapping("/{id}")
    public ResponseEntity<?> detailedSlide(@PathVariable String id){
        try{
            SlidesEntityDTO slide = slidesService.findById(id);

            return ResponseEntity.ok().body(slide);
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}