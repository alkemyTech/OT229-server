package com.alkemy.ong.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryListResponse {

    private List<String> categories = new ArrayList<>();

    public boolean addCategory(String category) {
        return this.categories.add(category);
    }

}
