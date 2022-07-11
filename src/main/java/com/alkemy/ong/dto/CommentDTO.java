package com.alkemy.ong.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
@Getter
@Setter
@Accessors(chain = true)
public class CommentDTO {
  private String id;
  @NotNull(message ="User is mandatory")
  private String user_id;
  @NotNull(message = "News is mandatory")
  private String post_id;
  @NotNull(message = "Body is mandatory")
  private String body;

}
