package com.alkemy.ong.security.payload;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Email;

@Getter
@Setter
public class SignupRequest {
    private String firstName;
    private String lastName;

    @Email(message = "Invalid email format")
    private String email;
    private String password;
}
