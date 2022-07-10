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
    
    
    @GetMapping("/post/{id}")
    public ResponseEntity<?> commentListOfAPost(@PathVariable String id){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(commentService.commentList(id));
        }catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(@PathVariable String id, @RequestParam(value = "commentBody", required = true) String commentBody,
                                           @RequestHeader("authorization") String token) throws Exception{
        try{
            return ResponseEntity.status(HttpStatus.OK).body(commentService.updateComment(id, commentBody, token));
        }catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable String id, @RequestHeader("authorization") String token) throws Exception {
        try{
            return ResponseEntity.status(HttpStatus.OK).body(commentService.deleteComment(id, token));
        }catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}
