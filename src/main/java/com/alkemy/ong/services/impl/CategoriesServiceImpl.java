package com.alkemy.ong.services.impl;

import com.alkemy.ong.dto.CategoryDTO;
import com.alkemy.ong.dto.PageResultResponse;
import com.alkemy.ong.entities.Category;
import com.alkemy.ong.exception.PageIndexOutOfBoundsException;
import com.alkemy.ong.mappers.CategoryMapper;
import com.alkemy.ong.mappers.PageResultResponseBuilder;
import com.alkemy.ong.repositories.CategoryRepository;
import com.alkemy.ong.repositories.NewsRepository;
import com.alkemy.ong.services.CategoriesService;
import com.alkemy.ong.services.CategoryEntityProvider;
import com.alkemy.ong.utility.GlobalConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoriesServiceImpl implements CategoriesService, CategoryEntityProvider {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private NewsRepository newsRepository;

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
        } else if (entitySameName.isPresent() && !entitySameName.get().getId().equals( entityFound.get().getId() )) {
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

    @Override
    public PageResultResponse<String> getAllCategoryNames(int pageNumber) throws PageIndexOutOfBoundsException {
        if (pageNumber < 0) {
            throw new PageIndexOutOfBoundsException("Page number must be positive.");
        }
        Pageable pageRequest = PageRequest.of(
                pageNumber,
                GlobalConstants.GLOBAL_PAGE_SIZE,
                Sort.by(GlobalConstants.CATEGORY_SORT_ATTRIBUTE)
        );
        Page<Category> springDataResultPage = this.categoryRepository.findAll(pageRequest);
        return new PageResultResponseBuilder<Category, String>()
                .from(springDataResultPage)
                .mapWith(Category::getName)
                .build();
    }

    @Transactional
    @Override
    public void deleteCategory(String id) {
        this.newsRepository.detachCategory(id);
        Optional<Category>entity = this.categoryRepository.findById(id);
        if(entity.isEmpty()){
            throw new RuntimeException("Category not present");
        }
        this.categoryRepository.delete(entity.get());
    }

    @Override
    public Optional<Category> getEntityByName(String name) {
        return this.categoryRepository.findByName(name);
    }


}
