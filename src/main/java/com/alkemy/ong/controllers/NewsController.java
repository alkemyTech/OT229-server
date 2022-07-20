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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

@RestController
@RequestMapping(GlobalConstants.Endpoints.NEWS)
public class  NewsController {

  @Autowired
  private NewsService newsService;
  @Autowired
  private CloudStorageService cloudStorageService;


  @Operation(summary = "Create and save a new News entity")
  @ApiResponses(
          value = {
                  @ApiResponse(responseCode = "200", description = "Return the News entity created successfully", content = {
                          @Content(mediaType = "application/json", schema = @Schema(implementation = NewsDTO.class))})
          }
  )
  @PostMapping
  public ResponseEntity<NewsDTO> save(@Valid @RequestBody NewsDTORequest newsDTO) throws Exception{
    return ResponseEntity.status(HttpStatus.CREATED).body(this.newsService.save(newsDTO));
  }

  @Operation(summary = "Search a news with an ID")
  @ApiResponses(
          value = {
                  @ApiResponse(responseCode = "200", description = "Returns the News found in the database with the provided ID", content = {
                          @Content(mediaType = "application/json", schema = @Schema(implementation = NewsDTO.class))}),

                  @ApiResponse(responseCode = "404", description = "News not found in the database with the provided ID", content = {
                          @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))})
          }
  )
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

  @Operation(summary = "Delete a New with an ID")
  @ApiResponses(
          value = {
                  @ApiResponse(responseCode = "200", description = "Return the News that was deleted", content = {
                          @Content(mediaType = "application/json", schema = @Schema(implementation = NewsDTO.class))}),

                  @ApiResponse(responseCode = "404", description = "News not found in the database with the provided ID", content = {
                          @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))})
          }
  )
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteNews(@PathVariable String id) throws CloudStorageClientException {
    try {
      NewsDTO newsDTO = this.newsService.deleteNews(id);
      return ResponseEntity.ok(new DeleteEntityResponse("News successfully deleted.", newsDTO));
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

  @Operation(summary = "Udpate a News with an ID")
  @ApiResponses(
          value = {
                  @ApiResponse(responseCode = "200", description = "Return the News that was updated", content = {
                          @Content(mediaType = "application/json", schema = @Schema(implementation = NewsDTO.class))}),

                  @ApiResponse(responseCode = "404", description = "News not found in the database with the provided ID", content = {
                          @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))}),

                  @ApiResponse(responseCode = "400", description = "Invalid model attribute", content = {
                          @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))})
          }
  )
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

  @Operation(summary = "Get all news")
  @ApiResponses(
          value = {
                  @ApiResponse(responseCode = "200", description = "Returns all news founded", content = {
                          @Content(mediaType = "application/json", schema = @Schema(implementation = NewsDTO.class))})
          }
  )
  @GetMapping
  public ResponseEntity<?> getAllNews(@RequestParam(value = GlobalConstants.PAGE_INDEX_PARAM) int page) throws PageIndexOutOfBoundsException {
    return ResponseEntity.ok( this.newsService.getAllNews(page) );
  }

}
