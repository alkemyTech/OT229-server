package com.alkemy.ong.security.payload;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "The User's email address.", example = "estebanquito@gmail.com")
    private String username;

    @NotEmpty(message = "Password not provided")
    @Size(min = 1, max = 255)
    @Schema(description = "The password for the account. All Unicode characters allowed.", example = "1234")
    private String password;

}
