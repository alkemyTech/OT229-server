package com.alkemy.ong.controllers;

import com.alkemy.ong.services.CommentService;
import com.alkemy.ong.utility.GlobalConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;

@RestController
@RequestMapping(GlobalConstants.Endpoints.COMMENTS)
public class CommentController {

    @Autowired
    CommentService commentService;

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable String id, @RequestHeader("authorization") String token) throws Exception {
        try{
            commentService.deleteComment(id, token);
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        }catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}
