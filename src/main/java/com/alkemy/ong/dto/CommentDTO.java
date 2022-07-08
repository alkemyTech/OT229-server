package com.alkemy.ong.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
@Getter
@Setter
public class CommentDTO {
  private String id;
  @NotNull(message ="User is mandatory")
  private String userId;
  @NotNull(message = "News is mandatory")
  private String newsId;
  @NotNull(message = "Body is mandatory")
  private String body;

}
