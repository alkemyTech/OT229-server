package com.alkemy.ong.security.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * DTO to map the login request body
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoginRequest {

    @NotEmpty(message = "Username not provided")
    @Email(message = "Invalid email format")
    private String username;

    @NotEmpty(message = "Password not provided")
    @Size(min = 1, max = 255)
    private String password;

}
