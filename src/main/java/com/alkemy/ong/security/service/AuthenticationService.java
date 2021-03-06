package com.alkemy.ong.security.service;

import com.alkemy.ong.entities.User;
import com.alkemy.ong.security.payload.LoginResponse;
import org.springframework.security.core.AuthenticationException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Handles the business logic for user authentication
 */
public interface AuthenticationService {

    /**
     * Authenticates a User into the session using Spring Security.
     *
     * @param username  the user's username (in this build, the user's email address).
     * @param password  the user's password (encoded).
     * @return  the AuthenticationService instance for further method chaining.
     * @throws AuthenticationException  if the user's credentials are not valid.
     */
    AuthenticationService authenticate(String username, String password) throws AuthenticationException;

    /**
     * Retrieves the User instance that is authenticated in the security context for the current session.
     *
     * @return an instance of the User authenticated in the current session.
     * @throws IllegalStateException if a user is currently authenticated in the security context but its
     *                               corresponding entity can't be retrieved from the database.
     */
    Optional<LoginResponse> getAuthenticatedUser() throws IllegalStateException;

    /**
     * Confirms whether the provided id matches the id from the currently authenticated User.
     *
     * @param id    the id to be compared.
     * @return  <code>true</code> if the ids match, <code>false</code> otherwise.
     * @throws IllegalStateException if a user is currently authenticated in the security context but its
     *                               corresponding entity can't be retrieved from the database.
     */
    boolean authUserMatchesId(String id) throws IllegalStateException;

    Optional<User> getAuthenticatedUserEntity() throws IllegalStateException;
}
