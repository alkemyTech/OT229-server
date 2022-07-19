package com.alkemy.ong.security.controller;

import com.alkemy.ong.dto.UserDTO;
import com.alkemy.ong.exception.CloudStorageClientException;
import com.alkemy.ong.exception.CorruptedFileException;
import com.alkemy.ong.exception.RegisterException;
import com.alkemy.ong.security.payload.*;
import com.alkemy.ong.security.service.AuthenticationService;
import javassist.NotFoundException;
import com.alkemy.ong.services.UserService;
import com.alkemy.ong.utility.GlobalConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import javax.validation.Valid;
import java.io.IOException;


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
                            @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "The email is already in use"))
                    }),
            @ApiResponse(responseCode="502", description = "Bad gateway: there was a problem with the email client.",
                    content = {
                            @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Email client error."))
                    })
    })
    @PostMapping(GlobalConstants.Endpoints.REGISTER)

    public ResponseEntity<?> register(@RequestBody @Valid SignupRequest signupRequest) throws CloudStorageClientException, CorruptedFileException {
          try {
              SingupResponse response = userService.createUser(signupRequest);

              return new ResponseEntity(response, HttpStatus.CREATED);
          } catch (RegisterException e){
              return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
          }catch (IOException e) {
              return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(e.getMessage());
          }
    }

    @Operation(summary = "Performs user authentication via login.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authentication.",
                    content = {
                            @Content(mediaType = "application/json", schema=@Schema(implementation = LoginResponse.class))
                    }),
            @ApiResponse(responseCode="401", description = "Unauthorized: bad credentials.",
                    content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleStatusResponse.class))
                    }),
            @ApiResponse(responseCode="500", description = "A user is authenticated but can't be retrieved from the database.",
                    content = {
                            @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "A user is authenticated but can't be retrieved from the database."))
                    })
    })
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body( new SimpleStatusResponse("false") );
        } catch (IllegalStateException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @Operation(summary = "Retrieves the information of the currently authenticated User.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK.",
                    content = {
                            @Content(mediaType = "application/json", schema=@Schema(implementation = UserDTO.class))
                    }),

            @ApiResponse(responseCode = "404", description = "Not found",
                    content = {
                            @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))
                    })
    })
    @GetMapping(GlobalConstants.Endpoints.AUTH_ME)
    public ResponseEntity<?> getMe(
            @Parameter(name = "authorization",
                    description = "A valid User's JWT access token.",
                    in = ParameterIn.HEADER,
                    example = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJVc2VybmFtZSI6ImVzdGViYW5xdWl0b0BnbWFpbC5jb20iLCJpc3MiOiJPTkcgLSBPVDIyOSIsIlJvbGVzIjpbIlJPTEVfVVNFUiJdLCJleHAiOjE2NTgwNzY4ODF9.fTrKT-5GGQBSy68NYFmOQCBeQZkq9k4FvR2JwfB2HB0-cOJMa-G9sMajrxKrpRGt2Y8nFWQRQDJZ_vsPBwRuOQ")
            @RequestHeader("authorization") String jwt) throws Exception{

        try{
            return new ResponseEntity<>(userService.getMe(jwt), HttpStatus.OK);
        }catch (NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
}
