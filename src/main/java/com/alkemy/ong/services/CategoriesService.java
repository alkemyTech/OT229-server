package com.alkemy.ong.services;
import com.alkemy.ong.dto.CategoryDTO;

import java.util.List;


public interface CategoriesService {

    public CategoryDTO getById (String id);

    public CategoryDTO save (CategoryDTO dto);

    public CategoryDTO edit (CategoryDTO dto, String id);

    List<String> getAllCategoryNames();

    void deleted(String id);
    
}

