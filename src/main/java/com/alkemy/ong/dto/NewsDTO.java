package com.alkemy.ong.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class NewsDTO {
  private String id;

  @NotEmpty(message = "name must be provided")
  @Schema(description = "The News's name.", example = "Weather today")
  private String name;

  @NotEmpty(message = "content must be provided")
  @Schema(description = "The News's content.", example = "Today will be sunny")
  private String content;

  private String image;

  @NotNull(message = "category must be provided")
  @Schema(description = "Category to which the news belongs", example = "Weather")
  private CategoryDTO category;

}
