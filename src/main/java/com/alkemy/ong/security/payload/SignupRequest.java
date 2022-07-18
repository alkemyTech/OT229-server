package com.alkemy.ong.security.payload;

import com.alkemy.ong.dto.EncodedImageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class SignupRequest {

    @NotEmpty(message = "Parameter 'firstName' should be complete.")
    @Schema(description = "The User's first name.", example = "John")
    private String firstName;

    @NotEmpty(message = "Parameter 'lastName' should be complete.")
    @Schema(description = "The User's last name.", example = "Doe")
    private String lastName;

    @NotEmpty
    @Email(message = "Invalid email format")
    @Schema(description = "The User's email address.", example = "john.doe@gmail.com")
    private String email;

    @NotEmpty(message = "Password cant be blank")
    @Schema(description = "The password for the account. All Unicode characters allowed.", example = "Pa55W#_r-.d")
    private String password;

    @Valid
    @Schema(description = "Associated image encoded using base64")
    private EncodedImageDTO encoded_image;

}
