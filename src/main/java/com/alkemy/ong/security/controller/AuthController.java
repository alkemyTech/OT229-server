package com.alkemy.ong.security.controller;

import com.alkemy.ong.dto.TestimonialDTOResponse;
import com.alkemy.ong.dto.UserDTO;
import com.alkemy.ong.exception.CloudStorageClientException;
import com.alkemy.ong.exception.CorruptedFileException;
import com.alkemy.ong.exception.RegisterException;
import com.alkemy.ong.security.payload.LoginRequest;
import com.alkemy.ong.security.payload.SingupResponse;
import com.alkemy.ong.security.service.AuthenticationService;
import com.alkemy.ong.services.UserService;
import com.alkemy.ong.utility.GlobalConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import com.alkemy.ong.security.payload.SignupRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;


@RestController
@ResponseStatus
public class AuthController {

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private UserService userService;

    @Operation(summary = "Register a new user account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Account successfully created.",
                    content = {
                            @Content(mediaType = "application/json", schema=@Schema(implementation = SingupResponse.class))
                    }),
            @ApiResponse(responseCode="409", description = "Conflict: email already taken.",
                    content = {
                            @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))
                    }),
            @ApiResponse(responseCode="502", description = "Bad gateway: there was a problem with the email client.",
                    content = {
                            @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))
                    })
    })
    @PostMapping(GlobalConstants.Endpoints.REGISTER)
    public ResponseEntity<?> register(@RequestBody @Valid SignupRequest signupRequest) throws CloudStorageClientException, CorruptedFileException {
          try {
              SingupResponse response = userService.createUser(signupRequest);

              return new ResponseEntity(response, HttpStatus.CREATED);
          } catch (RegisterException e){
              return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
          } catch (IOException e) {
              return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getMessage());
          }
    }

    @PostMapping(value = GlobalConstants.Endpoints.LOGIN, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> login(@Valid LoginRequest loginForm) {

        try {
            return ResponseEntity.ok(
                    this.authenticationService
                            .authenticate(loginForm.getUsername(), loginForm.getPassword())
                            .getAuthenticatedUser()
                            .get()
            );
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body( new HashMap<String, String>().put("ok", "false") );
        } catch (IllegalStateException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping(GlobalConstants.Endpoints.AUTH_ME)
    public ResponseEntity<UserDTO> getMe(@RequestHeader("authorization") String jwt) throws Exception{
        return new ResponseEntity<>(userService.getMe(jwt), HttpStatus.OK);
    }
    
}
