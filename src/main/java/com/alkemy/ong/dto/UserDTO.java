package com.alkemy.ong.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class UserDTO {

    @Schema(description = "The User's first name.", example = "John")
    private String firstName;
    @Schema(description = "The User's last name.", example = "Doe")
    private String lastName;
    @Schema(description = "The User's email address.", example = "john.doe@gmail.com")
    private String email;
    @Schema(description = "The url to access the associated user image.", example = "https://cohorte-junio-a192d78b.s3.amazonaws.com/1657985362232-test.png")
    private String image;

}
