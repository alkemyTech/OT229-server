package com.alkemy.ong.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class ContactDTORequest {

    @NotEmpty(message = "Email not provided")
    @Email(message = "Invalid email format")
    private String email;

    @NotEmpty(message = "Name not provided")
    private String name;

    private Long phone;

    private String message;
}
