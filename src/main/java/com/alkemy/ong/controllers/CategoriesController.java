package com.alkemy.ong.controllers;

import com.alkemy.ong.dto.CategoryDTO;
import com.alkemy.ong.dto.CategoryListResponse;
import com.alkemy.ong.dto.DeleteEntityResponse;
import com.alkemy.ong.services.CategoriesService;
import com.alkemy.ong.utility.GlobalConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(GlobalConstants.Endpoints.CATEGORIES)
public class CategoriesController {

    @Autowired
    private CategoriesService categoriesService;

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

    
    @GetMapping
    public ResponseEntity<?> getCategoryList() {
        CategoryListResponse responseBody = new CategoryListResponse();
        responseBody.setCategories(this.categoriesService.getAllCategoryNames());
        return ResponseEntity.ok(responseBody);

    }

    @DeleteMapping("{/id}")
    public ResponseEntity<?> delete(@PathVariable String id){
        try {
            categoriesService.deleteCategory(id);
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
