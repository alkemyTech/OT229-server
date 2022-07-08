package com.alkemy.ong.controllers;

import com.alkemy.ong.dto.CommentDTO;
import com.alkemy.ong.services.CommentService;
import com.alkemy.ong.utility.GlobalConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(GlobalConstants.Endpoints.COMMENTS)
public class CommentController {
  @Autowired
  private CommentService commentService;

  @PostMapping
  public ResponseEntity<CommentDTO> save (@Valid @RequestBody CommentDTO commentDTO)throws Exception{
    CommentDTO comment = commentService.save(commentDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(comment);
  }
}
