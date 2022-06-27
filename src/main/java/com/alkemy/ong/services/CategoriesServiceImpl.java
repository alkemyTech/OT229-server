package com.alkemy.ong.services;

import com.alkemy.ong.dto.CategoryDTO;
import com.alkemy.ong.entities.Category;
import com.alkemy.ong.mappers.CategoryMapper;
import com.alkemy.ong.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoriesServiceImpl implements CategoriesService{

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

    @Override
    public List<String> getAllCategoryNames() {
        return this.categoryRepository.findAllByOrderByName()
                .stream()
                .map(Category::getName)
                .collect(Collectors.toList());
    }
}
