package com.alkemy.ong.mappers;

import com.alkemy.ong.dto.CategoryDTO;
import com.alkemy.ong.entities.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryDTO categoryEntity2DTO (Category entity) {
        CategoryDTO dto = new CategoryDTO();

        dto.setName(entity.getName());
        dto.setImage(entity.getImage());
        dto.setDescription(entity.getDescription());

        return dto;
    }
}
