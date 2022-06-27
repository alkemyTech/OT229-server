package com.alkemy.ong.controllers;

import com.alkemy.ong.dto.CategoryDTO;
import com.alkemy.ong.dto.CategoryListResponse;
import com.alkemy.ong.dto.OrganizationDTO;
import com.alkemy.ong.services.CategoriesService;
import com.alkemy.ong.utility.GlobalConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
    
    @GetMapping
    public ResponseEntity<?> getCategoryList() {
        CategoryListResponse responseBody = new CategoryListResponse();
        responseBody.setCategories(this.categoriesService.getAllCategoryNames());
        return ResponseEntity.ok(responseBody);
    }
}
