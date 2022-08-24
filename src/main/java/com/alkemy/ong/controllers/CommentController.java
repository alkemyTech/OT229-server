package com.alkemy.ong.controllers;

import com.alkemy.ong.dto.CommentDTO;
import com.alkemy.ong.services.CommentService;
import com.alkemy.ong.utility.GlobalConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

@RestController
public class CommentController {

    @Autowired
    CommentService commentService;

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(GlobalConstants.Endpoints.COMMENTS)
    public ResponseEntity<?> save (@Valid @RequestBody CommentDTO commentDTO) {
        try {
            CommentDTO comment = commentService.save(commentDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(comment);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/post/{id}/" + GlobalConstants.Endpoints.COMMENTS)
    public ResponseEntity<?> commentListOfAPost(@PathVariable String id) throws Exception {
        try{
            return ResponseEntity.status(HttpStatus.OK).body(commentService.commentList(id));
        }catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping(GlobalConstants.Endpoints.COMMENTS + "/{id}")
    public ResponseEntity<?> updateComment(@PathVariable String id, @RequestParam(value = "commentBody", required = true) String commentBody) throws Exception{
        try{
            return ResponseEntity.status(HttpStatus.OK).body(commentService.updateComment(id, commentBody));
        }catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping(GlobalConstants.Endpoints.COMMENTS + "/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable String id) throws Exception {
        try{
            return ResponseEntity.status(HttpStatus.OK).body(commentService.deleteComment(id));
        }catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(GlobalConstants.Endpoints.COMMENTS)
    public ResponseEntity<?> getAllComments(){
        return new ResponseEntity<>(commentService.getAll(),HttpStatus.OK);
    }

}
