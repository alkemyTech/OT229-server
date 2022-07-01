package com.alkemy.ong.controllers;

import com.alkemy.ong.dto.DeleteEntityResponse;
import com.alkemy.ong.dto.NewsDTO;
import com.alkemy.ong.services.CloudStorageService;
import com.alkemy.ong.services.NewsService;
import com.alkemy.ong.utility.GlobalConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping(GlobalConstants.Endpoints.NEWS)
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
  public ResponseEntity<?> findById(@PathVariable String id){
    NewsDTO newsDTO=null;
    try {
      newsDTO = newsService.findById(id);
    }catch (RuntimeException e){
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

      return ResponseEntity.status(HttpStatus.OK).body(newsDTO);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteNews(@PathVariable String id) {
    try {
      NewsDTO newsDTO = this.newsService.deleteNews(id);
      return ResponseEntity.ok(new DeleteEntityResponse("News successfully deleted.", newsDTO));
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (IOException cloudStorageServiceProblemException) {
      return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("There was a problem trying do delete the associated images. Please try again later.");
    }
  }

}
