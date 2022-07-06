package com.alkemy.ong.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class NewsDTO {
  private String id;
  @NotEmpty(message = "name must be provided")
  private String name;
  @NotEmpty(message = "content must be provided")
  private String content;

  private String image;
  @NotNull(message = "category must be provided")
  private CategoryDTO category;

}
