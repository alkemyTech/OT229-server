package com.alkemy.ong.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class NewsDTO {
    private String id;
    @NotBlank
    private String name;
    @NotBlank
    private String content;
    @NotBlank
    private String image;
}
