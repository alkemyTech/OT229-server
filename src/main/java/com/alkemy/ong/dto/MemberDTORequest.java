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
public class MemberDTORequest {

    @NotEmpty(message = "Name not provided")
    private String name;

    private String facebookUrl;

    private String instagramUrl;

    private String linkedinUrl;

    private String description;

    private String image;

    private EncodedImageDTO encoded_image;

}
