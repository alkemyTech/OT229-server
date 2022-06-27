package com.alkemy.ong.security.payload;

import com.alkemy.ong.dto.UserDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class LoginResponse {
    private String message;
    private UserDTO user;
    private String token;
}
