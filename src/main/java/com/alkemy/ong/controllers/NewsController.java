package com.alkemy.ong.controllers;

import com.alkemy.ong.dto.DeleteEntityResponse;
import com.alkemy.ong.dto.NewsDTO;
import com.alkemy.ong.dto.NewsDTORequest;
import com.alkemy.ong.exception.CloudStorageClientException;
import com.alkemy.ong.exception.CorruptedFileException;
import com.alkemy.ong.exception.PageIndexOutOfBoundsException;
import com.alkemy.ong.services.CloudStorageService;
import com.alkemy.ong.services.NewsService;
import com.alkemy.ong.utility.GlobalConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
  public ResponseEntity<NewsDTO> save(@Valid @RequestBody NewsDTORequest newsDTO) throws Exception{
    return ResponseEntity.status(HttpStatus.CREATED).body(this.newsService.save(newsDTO));
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
  public ResponseEntity<?> deleteNews(@PathVariable String id) throws CloudStorageClientException {
    try {
      NewsDTO newsDTO = this.newsService.deleteNews(id);
      return ResponseEntity.ok(new DeleteEntityResponse("News successfully deleted.", newsDTO));
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateNews(@Valid @RequestBody NewsDTORequest updatedNews,
                                      @PathVariable String id) throws CloudStorageClientException, CorruptedFileException {

    try {
      return ResponseEntity.ok(this.newsService.updateNews(id, updatedNews));
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }

  @GetMapping
  public ResponseEntity<?> getAllNews(@RequestParam(value = GlobalConstants.PAGE_INDEX_PARAM) int page) throws PageIndexOutOfBoundsException {
    return ResponseEntity.ok( this.newsService.getAllNews(page) );
  }

}
