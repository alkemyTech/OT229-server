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
public class MemberDTOResponse {
    @NotEmpty(message = "Name not provided")
    @Schema(description = "The member's name", example = "John Doe")
    private String name;
    @Schema(description = "The member's Facebook's URL", example = "https://www.facebook.com/profile.php?id=10001166820532")
    private String facebookUrl;
    @Schema(description = "The member's Instagram's URL", example = "https://www.instagram.com/johndoe/")
    private String instagramUrl;
    @Schema(description = "The member's Linkedin's URL", example = "https://www.linkedin.com/in/john-doe-8613831b9/")
    private String linkedinUrl;
    @Schema(description = "The URL of the member's image saved in the cloud service", example = "https://cohorte-junio-a192d78b.s3.amazonaws.com/1657462732290-ezeTest.txt")
    private String image;
    @Schema(description = "The member's description", example = "I am a very hardworking person who...")
    private String description;

}
