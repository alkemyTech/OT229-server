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

    @Schema(description = "Entity id", example = "3b6f64ed-ecaa-4ae1-9e97-091464bc8dc1")
    private String id;
    @Schema(description = "The name/title of the testimonial", example = "Testimonial from Juan, Director of the local high school.")
    private String name;
    @Schema(description = "Image that accompanies the testimonial", example = "com.image.jpg")
    private String image;
    @Schema(description = "Body of the testimonial", example = "The mentorship program has been very successful, the numbers are...")
    private String content;

}
