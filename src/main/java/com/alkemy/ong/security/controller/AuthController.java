package com.alkemy.ong.security.controller;

import com.alkemy.ong.entities.User;
import com.alkemy.ong.security.payload.LoginRequest;
import com.alkemy.ong.security.service.AuthenticationService;
import com.alkemy.ong.services.EmailService;
import com.alkemy.ong.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alkemy.ong.security.payload.SignupRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.Valid;
import java.util.HashMap;


@RestController
@RequestMapping("/auth")
@ResponseStatus

public class AuthController {

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody SignupRequest signupRequest) {
      try {
          User user = new User();
          user.setFirstName(signupRequest.getFirstname());
          user.setLastName(signupRequest.getLastname());
          user.setEmail(signupRequest.getEmail());
          user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
          userService.save(user);
          emailService.sendEmail(signupRequest.getEmail());
          return new ResponseEntity(user, HttpStatus.CREATED);
      } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
      }
    }

    @PostMapping("/login")
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
