package com.alkemy.ong.services.impl;

import com.alkemy.ong.dto.CategoryDTO;
import com.alkemy.ong.entities.Category;
import com.alkemy.ong.mappers.CategoryMapper;
import com.alkemy.ong.repositories.CategoryRepository;
import com.alkemy.ong.services.CategoriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoriesServiceImpl implements CategoriesService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    public CategoryDTO getById(String id) {
        Optional<Category> entity = categoryRepository.findById(id);
        if (!entity.isPresent()) {
            throw new RuntimeException("Category with the provided ID not present");
        }
        CategoryDTO dto = categoryMapper.categoryEntity2DTO(entity.get());
        return dto;
    }


    public CategoryDTO save(CategoryDTO dto) {
        Optional <Category> entityFound = categoryRepository.findByName(dto.getName());
        if(entityFound.isPresent()) {
            throw new RuntimeException("Category with the provided name is already present over the system");
        }
        Category entity = categoryMapper.categoryDTO2Entity(dto);
        Category entitySaved = categoryRepository.save(entity);

        CategoryDTO dtoReturn = categoryMapper.categoryEntity2DTO(entitySaved);

        return dtoReturn;
    }
    
    @Override
    public List<String> getAllCategoryNames() {
        return this.categoryRepository.findAllByOrderByName()
                .stream()
                .map(Category::getName)
                .collect(Collectors.toList());
    }
}
