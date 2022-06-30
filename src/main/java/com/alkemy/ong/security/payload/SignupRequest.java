package com.alkemy.ong.security.payload;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class SignupRequest {
    @NotBlank(message = "Parameter 'firstName' should be complete.")
    private String firstName;

    @NotBlank(message = "Parameter 'lastName' should be complete.")
    private String lastName;

    @NotBlank
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password cant be blank")
    private String password;
}
