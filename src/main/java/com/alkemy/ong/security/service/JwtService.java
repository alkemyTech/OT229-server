package com.alkemy.ong.security.service;

import com.alkemy.ong.entities.User;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.List;

public interface JwtService {

    /**
     * This method return a JWT token with user's info.
     * - Username's user
     * - Date expiration
     * - User roles
     *
     * @Param Objet user
     * @return a JWT token with user's info.
     * */
    String createToken(User user);

    /**
     * This method check the HEADER of the token.
     *
     * @return true if HEADER start with "Bearer "
     * */
    Boolean isBearer(String token);

    /**
     * This method search the username claim in the JWT token
     * */
    String getUsername(String token) throws Exception;

    /**
     * This method search all roles in the JWT token
     *
     * @return user roles
     * */
    List<String> getRoles(String token) throws Exception;

    /**
     * This method decrypt the token, check the expiration date and the issuer
     *
     * @return JWT token decrypted
     * */
    DecodedJWT verify(String token) throws Exception;
}
