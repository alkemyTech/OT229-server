package com.alkemy.ong.controllers;

import com.alkemy.ong.dto.NewsDTO;
import com.alkemy.ong.services.CloudStorageService;
import com.alkemy.ong.services.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("news")
public class NewsController {

  @Autowired
  private NewsService newsService;
  @Autowired
  private CloudStorageService cloudStorageService;


  @PostMapping
  public ResponseEntity<NewsDTO> save(@RequestParam(value = "file",required = false) MultipartFile file, @ModelAttribute NewsDTO newsDTO) throws Exception{

    newsDTO.setImage(cloudStorageService.uploadFile(file));
    return ResponseEntity.status(HttpStatus.CREATED).body(this.newsService.save(file,newsDTO));

  }

  @GetMapping("/{id}")
  public ResponseEntity<NewsDTO> findById(@PathVariable String id){
      NewsDTO newsDTO = newsService.findById(id);
      return ResponseEntity.status(HttpStatus.OK).body(newsDTO);
  }
}
