package com.alkemy.ong.controllers;

import com.alkemy.ong.dto.NewsDTO;
import com.alkemy.ong.services.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("news")
public class NewsConstroller {

    @Autowired
    private NewsService newsService;

   @PostMapping
    public ResponseEntity<NewsDTO> save(@Valid @RequestBody NewsDTO newsDTO){
       NewsDTO newsSaved = this.newsService.save(newsDTO);
       return ResponseEntity.status(HttpStatus.CREATED).body(newsSaved);
   }


}
