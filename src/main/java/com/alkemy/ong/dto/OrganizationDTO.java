package com.alkemy.ong.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Getter
@Setter
public class OrganizationDTO {

    @NotBlank
    private String id;
    @NotBlank
    private String name;
    @NotBlank
    private String image;
    private Integer phone;
    private String address;
    private String urlFacebook;
    private String urlInstagram;
    private String urlLinkedin;

    @NotBlank
    @Email(message = "Invalid email format")
    private String email;
    @NotBlank
    private String welcomeText;
    private String aboutUsText;

}
