package com.alkemy.ong.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;

@NoArgsConstructor
@Getter
@Setter
public class OrganizationDTO {
    private String id;
    private String name;
    private String image;
    private Integer phone;
    private String address;
    private String urlFacebook;
    private String urlInstagram;
    private String urlLinkedin;

    @Email(message = "Invalid email format")
    private String email;
    private String welcomeText;
    private String aboutUsText;
}
