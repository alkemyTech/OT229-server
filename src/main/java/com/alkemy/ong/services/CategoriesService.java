package com.alkemy.ong.services;
import com.alkemy.ong.dto.CategoryDTO;


public interface CategoriesService {

    public CategoryDTO getById (String id);

    public CategoryDTO save (CategoryDTO dto);

    public CategoryDTO edit (CategoryDTO dto, String id);
}