package com.alkemy.ong.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TestimonialDTOResponse {
    @Schema(description = "The name of the testimonial", example = "Juan testimonial", required = true)
    private String name;
    @Schema(description = "Image that represents the testimonial", example = "com.image.jpg")
    private String image;
    @Schema(description = "Complete description of the testimonial", example = "This ong experience...")
    private String content;
}
