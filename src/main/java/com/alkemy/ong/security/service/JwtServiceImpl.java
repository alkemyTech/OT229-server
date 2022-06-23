package com.alkemy.ong.security.service;

import com.alkemy.ong.entities.Role;
import com.alkemy.ong.entities.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JwtServiceImpl implements JwtService{

    private final String BEARER = "Bearer ";
    @Value("${token.issuer}")
    private String ISSUER;

    @Value("${token.secret.key}")
    private String SECRET_KEY;

    @Value("${token.expires}")
    private int EXPIRATION_TIME; // Tiempo de 30 minutos

    @Override
    public String createToken(User usuario) {

        List<String> roles = new ArrayList<>();
        for(Role rol: usuario.getRoleId()){
            roles.add(rol.getName());
        }

        return JWT.create().withIssuer(ISSUER)
                .withClaim("Username", usuario.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME ))
                .withArrayClaim("Roles", roles.toArray(new String[0]))
                .sign(Algorithm.HMAC512(SECRET_KEY));
    }

    @Override
    public Boolean isBearer(String token) {
        return token != null && token.startsWith(BEARER) && token.split("\\.").length == 3;
    }

    @Override
    public List<String> getRoles(String token) throws Exception {
        return Arrays.asList(this.verify(token).getClaim("Roles").asArray(String.class));
    }

    @Override
    public String getUsername(String token) throws Exception {
        return this.verify(token).getClaim("Username").asString();
    }

    @Override
    public DecodedJWT verify(String token) throws Exception{
        if(!this.isBearer(token)){
            throw new Exception("Isn't a Bearer");
        }
        try{
            return JWT.require(Algorithm.HMAC512(SECRET_KEY))
                    .withIssuer(ISSUER).build()
                    .verify(token.substring(BEARER.length()));

        }catch(Exception ex){
            throw new Exception("JWT wrong " + ex.getMessage());
        }
    }
}
