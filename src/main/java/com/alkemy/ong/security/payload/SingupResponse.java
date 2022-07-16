package com.alkemy.ong.security.payload;

import com.alkemy.ong.dto.UserDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SingupResponse {
    @Schema(description = "Response message.", example = "Successful registration")
    private String message;
    @Schema(description = "The created user account, as it was stored.")
    private UserDTO user;
    @Schema(description = "An access token to be used for authentication in secured operations.", example = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJVc2VybmFtZSI6ImpwZXJlekBnbWFpbC5jb20iLCJpc3MiOiJPTkcgLSBPVDIyOSIsIlJvbGVzIjpbIlJPTEVfQURNSU4iXSwiZXhwIjoxNjU4MDcxNTI1fQ.KOKxEPvnykaHuovikDY0MaNnY8fEQZAcg_btSRxl628-6x-djR-Xu1-LMKeUNAmeG5w9HHqFBcg1fFOGJOl8IA")
    private String token;
}
