package com.alkemy.ong.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class NewsDTO {
  private String id;
  @NotEmpty
  private String name;
  @NotEmpty
  private String content;
  @NotEmpty
  private String image;
  @NotEmpty
  private CategoryDTO category;

}
