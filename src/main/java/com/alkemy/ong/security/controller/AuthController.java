package com.alkemy.ong.security.controller;

import com.alkemy.ong.entities.User;
import com.alkemy.ong.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alkemy.ong.security.payload.SignupRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.Valid;


@RestController
@RequestMapping("/auth")
@ResponseStatus

public class AuthController {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody SignupRequest signupRequest) {
      try {
          User user = new User();
          user.setFirstName(signupRequest.getFirstname());
          user.setLastName(signupRequest.getLastname());
          user.setEmail(signupRequest.getEmail());
          user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
          userService.save(user);
          return new ResponseEntity(user, HttpStatus.CREATED);
      } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
      }
    }
    
    
}
