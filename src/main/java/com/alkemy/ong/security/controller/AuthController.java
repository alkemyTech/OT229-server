package com.alkemy.ong.security.controller;

import com.alkemy.ong.dto.UserDTO;
import com.alkemy.ong.security.payload.LoginRequest;
import com.alkemy.ong.security.payload.SingupResponse;
import com.alkemy.ong.security.service.AuthenticationService;
import com.alkemy.ong.services.UserService;
import com.alkemy.ong.utility.GlobalConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import com.alkemy.ong.security.payload.SignupRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.Valid;
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

    @PostMapping(GlobalConstants.Endpoints.REGISTER)
    public ResponseEntity<?> register(@RequestParam(value="file", required = false) MultipartFile image,
                                      @ModelAttribute SignupRequest signupRequest) {
      try {
          SingupResponse response = userService.createUser(signupRequest, image);

          return new ResponseEntity(response, HttpStatus.CREATED);
      } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
      }
    }

    @PostMapping(GlobalConstants.Endpoints.LOGIN)
    public ResponseEntity<?> login(@Valid LoginRequest loginForm) {

        System.out.println(loginForm.getUsername() + "  " + loginForm.getPassword());
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
    
}
