package com.alkemy.ong.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TestimonialDTORequest {

    @NotEmpty(message = "Name not provided")
    private String name;

    @NotEmpty(message = "Content not provided")
    private String content;

    private EncodedImageDTO encoded_image;

}
