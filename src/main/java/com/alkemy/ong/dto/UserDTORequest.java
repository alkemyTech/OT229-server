package com.alkemy.ong.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDTORequest {
    private String id;
    private String firstName;
    private String lastName;
    @Email(message = "Invalid email format")
    private String email;
    @Size(min = 1, max = 255)
    private String password;


}
