package com.alkemy.ong.security.service;

import com.alkemy.ong.entities.User;
import com.alkemy.ong.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;

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
    public Optional<User> getAuthenticatedUser() throws IllegalStateException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Optional.empty();
        }
        String authUserEmail = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal())
                .getUsername();
        User authUser = this.userService.getUserByEmail(authUserEmail)
                .orElseThrow(() -> new IllegalStateException("A user is authenticated but can't be retrieved from the database."));
        return Optional.of(authUser);
    }

}
