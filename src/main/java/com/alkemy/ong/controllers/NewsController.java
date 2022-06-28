package com.alkemy.ong.controllers;

import com.alkemy.ong.dto.NewsDTO;
import com.alkemy.ong.services.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping
public class NewsController {

  @Autowired
  private NewsService newsService;

  @PostMapping
  public ResponseEntity<NewsDTO> save(@RequestParam(value = "file",required = false) MultipartFile image, @Valid @RequestBody NewsDTO newsDTO) throws Exception{
    NewsDTO newsSaved = this.newsService.save(image,newsDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(newsSaved);
  }

}
