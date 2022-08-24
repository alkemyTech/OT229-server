package com.alkemy.ong.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Setter
@Getter
public class CategoryDTO {

    private String id;
    @NotBlank(message = "The Category name must not be blank")
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "The Category name has to be a String")
    @NotNull(message = "The Category must not be null")
    @Schema(description = "Category to which the news belongs", example = "Announcements")
    private String name;

    @Schema(description = "Category description", example = "New activity description")
    private String description;

    @Schema(description = "Image URL", example = "imgur.com/image.jpg")
    private String image;

}
