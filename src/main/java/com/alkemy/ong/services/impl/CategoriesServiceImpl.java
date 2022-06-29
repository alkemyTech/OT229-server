package com.alkemy.ong.services.impl;

import com.alkemy.ong.dto.CategoryDTO;
import com.alkemy.ong.entities.Category;
import com.alkemy.ong.mappers.CategoryMapper;
import com.alkemy.ong.repositories.CategoryRepository;
import com.alkemy.ong.services.CategoriesService;
import com.amazonaws.services.mq.model.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
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
        Optional<Category> entityFound = categoryRepository.findByName(dto.getName());
        if (entityFound.isPresent()) {
            throw new RuntimeException("Category with the provided name is already present over the system");
        }
        Category entity = categoryMapper.categoryDTO2Entity(dto);
        Category entitySaved = categoryRepository.save(entity);

        CategoryDTO dtoReturn = categoryMapper.categoryEntity2DTO(entitySaved);

        return dtoReturn;
    }



    public CategoryDTO edit(CategoryDTO dto, String id) {
        Optional<Category> entityFound = categoryRepository.findById(id);
        Optional<Category> entitySameName = categoryRepository.findByName(dto.getName());
        if (!entityFound.isPresent()) {
            throw new RuntimeException("Category with the provided ID not present");
        } else if (entitySameName.isPresent() && entitySameName.get().getId() != entityFound.get().getId()) {
            throw new RuntimeException("The name is already present over the system, please change it");
        }

        Category modifiedEntity = categoryMapper.editEntity(entityFound.get(), dto);
        categoryRepository.save(modifiedEntity);
        CategoryDTO result = categoryMapper.categoryEntity2DTO(modifiedEntity);

        return result;


    }

    
    @Override
    public List<String> getAllCategoryNames() {
        return this.categoryRepository.findAllByOrderByName()
                .stream()
                .map(Category::getName)
                .collect(Collectors.toList());

    }

    @Transactional
    @Override
    public void deleted(String id) {
        Optional<Category>entity = this.categoryRepository.findById(id);
        this.categoryRepository.delete(entity.get());
    }
}
