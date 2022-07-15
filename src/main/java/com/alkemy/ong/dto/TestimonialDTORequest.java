package com.alkemy.ong.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "The name of the testimonial", example = "Juan testimonial", required = true)
    @NotEmpty(message = "Name not provided")
    private String name;

    @Schema(description = "Complete description of the testimonial", example = "This ong experience...")
    @NotEmpty(message = "Content not provided")
    private String content;

    private EncodedImageDTO encoded_image;

}
