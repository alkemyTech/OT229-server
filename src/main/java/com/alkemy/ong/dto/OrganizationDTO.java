package com.alkemy.ong.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@NoArgsConstructor
@Getter
@Setter
public class OrganizationDTO {

    @Pattern(regexp = "^[a-zA-Z ]+$", message = "The organization name has to be a String")
    @Nullable
    private String name;

    @Pattern(regexp = "^[a-zA-Z ]+$", message = "The organization image has to be a String")
    @Nullable
    private String image;

    @Nullable
    private Integer phone;

    @Pattern(regexp = "^[a-zA-Z ]+$", message = "The organization address has to be a String")
    @Nullable
    private String address;

    @Email
    @Nullable
    private String email;

    @Pattern(regexp = "^[a-zA-Z ]+$", message = "The organization welcomeText has to be a String")
    @Nullable
    private String welcomeText;

    @Pattern(regexp = "^[a-zA-Z ]+$", message = "The organization aboutUsText has to be a String")
    @Nullable
    private String aboutUsText;
}
