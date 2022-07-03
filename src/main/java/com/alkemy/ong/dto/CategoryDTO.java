package com.alkemy.ong.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Setter
@Getter
public class CategoryDTO {

    @NotBlank(message = "The Category name must not be blank")
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "The Category name has to be a String")
    @NotNull(message = "The Category must not be null")
    private String name;

    private String description;

    private String image;

    private String id;

}