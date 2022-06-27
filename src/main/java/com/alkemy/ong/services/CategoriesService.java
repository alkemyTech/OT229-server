package com.alkemy.ong.services;
import com.alkemy.ong.dto.CategoryDTO;

import java.util.List;


public interface CategoriesService {

    public CategoryDTO getById (String id);

    public CategoryDTO save (CategoryDTO dto);
    
    List<String> getAllCategoryNames();
    
}
