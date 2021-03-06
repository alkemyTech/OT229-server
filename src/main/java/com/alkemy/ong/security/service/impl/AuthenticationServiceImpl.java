package com.alkemy.ong.security.service.impl;

import com.alkemy.ong.dto.UserDTO;
import com.alkemy.ong.entities.User;
import com.alkemy.ong.mappers.UserMapper;
import com.alkemy.ong.security.payload.LoginResponse;
import com.alkemy.ong.security.service.AuthenticationService;
import com.alkemy.ong.security.service.JwtService;
import com.alkemy.ong.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtService jwtService;

    @Autowired
    public AuthenticationServiceImpl(AuthenticationManager authenticationManager, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @Override
    public AuthenticationService authenticate(String username, String password) throws AuthenticationException {
        UsernamePasswordAuthenticationToken springAuthToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication springAuthentication  = this.authenticationManager.authenticate(springAuthToken);
        SecurityContextHolder.getContext().setAuthentication(springAuthentication);
        return this;
    }

    @Override
    public Optional<LoginResponse> getAuthenticatedUser() throws IllegalStateException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Optional.empty();
        }
        String authUserEmail = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal())
                .getUsername();
        User authUser = this.userService.getUserByEmail(authUserEmail)
                .orElseThrow(() -> new IllegalStateException("A user is authenticated but can't be retrieved from the database."));

        LoginResponse loginResponse = new LoginResponse();
        UserDTO userDTO = userMapper.userEntity2DTO(authUser);

        loginResponse.setUser(userDTO);
        String token = jwtService.createToken(authUser);
        loginResponse.setToken(token);
        loginResponse.setMessage("Successful Authentication.");

        return Optional.of(loginResponse);
    }

    @Override
    public boolean authUserMatchesId(String id) throws IllegalStateException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        String authUserEmail;
        try {
            authUserEmail = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal())
                    .getUsername();
        } catch (ClassCastException e) {
            authUserEmail = (String) authentication.getPrincipal();
        }
        User authUser = this.userService.getUserByEmail(authUserEmail)
                .orElseThrow(() -> new IllegalStateException("A user is authenticated but can't be retrieved from the database."));
        return authUser.getId().equals(id);
    }

    @Override
    public Optional<User> getAuthenticatedUserEntity() throws IllegalStateException{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Optional.empty();
        }

        String authUserEmail;
        try{
            authUserEmail = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal())
                .getUsername();
        } catch (ClassCastException e) {
            authUserEmail = (String) authentication.getPrincipal();
        }

        User authUser = this.userService.getUserByEmail(authUserEmail)
                .orElseThrow(() -> new IllegalStateException("A user is authenticated but can't be retrieved from the database."));

        return Optional.of(authUser);
    }
}
