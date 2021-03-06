package com.alkemy.ong.controllers;

import com.alkemy.ong.dto.*;
import com.alkemy.ong.exception.PageIndexOutOfBoundsException;
import com.alkemy.ong.services.CategoriesService;
import com.alkemy.ong.utility.GlobalConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(GlobalConstants.Endpoints.CATEGORIES)
public class CategoriesController {

    @Autowired
    private CategoriesService categoriesService;

    @Operation(summary = "Get details from a category",security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",description = "Returns details from a category",content = {@Content(mediaType = "application/json",schema = @Schema(implementation = CategoryDTO.class))}),
                    @ApiResponse(responseCode = "404",description = "Category not found",content = {@Content(mediaType = "application/json",schema = @Schema(implementation = String.class))})
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        CategoryDTO dto = null;
        try {
            dto = categoriesService.getById(id);
        } catch (RuntimeException r) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(r.getMessage());
        }

        return ResponseEntity.ok().body(dto);
    }

    @Operation(summary = "Create a category ",security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201",description = "Create and save a category",content = {@Content(mediaType = "application/json",schema = @Schema(implementation = CategoryDTO.class))}),
                    @ApiResponse(responseCode = "400",description = "Category with the provided name it already exists in the system",content = {@Content(mediaType = "application/json",schema = @Schema(implementation = String.class))})

            }
    )
    @PostMapping()
    public ResponseEntity<?> save(@Valid @RequestBody CategoryDTO dto) {
        CategoryDTO modifiedDTO = null;
        try {
            modifiedDTO = categoriesService.save(dto);
        } catch (RuntimeException r) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(r.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(modifiedDTO);
    }

    @Operation(summary = "Update a category",security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",description = "Returns an updated category",content = {@Content(mediaType = "application/json",schema = @Schema(implementation = CategoryDTO.class))}),
                    @ApiResponse(responseCode = "400",description = "Category with the provided ID not found",content = {@Content(mediaType = "application/json",schema = @Schema(implementation = String.class))})
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<?> edit(@Valid @RequestBody CategoryDTO dto, @PathVariable String id) {
        CategoryDTO modifiedDTO = null;
        try {
            modifiedDTO = categoriesService.edit(dto,id);
        } catch(RuntimeException r) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(r.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body(modifiedDTO);

    }
    @Operation(summary = "Get a list of categories",security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",description = "Returns a list of categories with their details",content = {@Content(mediaType = "application/json",schema = @Schema(implementation = CategoryDTO.class))})
            }
    )
    @GetMapping
    public ResponseEntity<?> getCategoryList(@RequestParam(value = GlobalConstants.PAGE_INDEX_PARAM) int page) throws PageIndexOutOfBoundsException {
        return ResponseEntity.ok(this.categoriesService.getAllCategoryNames(page));
    }

    @Operation(summary = "Delete a category",security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",description = "Just return an ok state"),
                    @ApiResponse(responseCode = "404",description = "Category with the provided ID not found",content = {@Content(mediaType = "application/json",schema = @Schema(implementation = String.class))})
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id){
        try {
            categoriesService.deleteCategory(id);
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
