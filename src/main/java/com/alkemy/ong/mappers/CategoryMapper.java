package com.alkemy.ong.mappers;

import com.alkemy.ong.dto.CategoryDTO;
import com.alkemy.ong.entities.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryDTO categoryEntity2DTO (Category entity) {
        CategoryDTO dto = new CategoryDTO();
        dto.setDescription(entity.getDescription());
        dto.setName(entity.getName());
        dto.setImage(entity.getImage());
        dto.setDescription(entity.getDescription());

        return dto;
    }

    public Category categoryDTO2Entity (CategoryDTO dto) {
        Category entity = new Category();
        entity.setDescription(dto.getDescription());
        entity.setImage(dto.getImage());
        entity.setName(dto.getName());

        return entity;
    }

    public Category editEntity (Category entity, CategoryDTO dto) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setImage(dto.getImage());

        return entity;
    }
}
