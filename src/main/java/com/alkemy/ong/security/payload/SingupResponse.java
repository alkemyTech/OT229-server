package com.alkemy.ong.security.payload;

import com.alkemy.ong.dto.UserDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SingupResponse {
    private String message;
    private UserDTO user;
    private String token;
}
