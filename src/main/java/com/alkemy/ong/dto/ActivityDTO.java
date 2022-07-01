package com.alkemy.ong.dto;


import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.*;


@Getter
@Setter

public class ActivityDTO {

    @NotEmpty(message = "The Activity name should be inputted")
    private String name;

    @NotEmpty(message = "The Activity content should be inputted")
    private String content;

    private String image;
}
