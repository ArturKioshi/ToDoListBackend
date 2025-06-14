package com.kioshi.todolist.security;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.kioshi.todolist.exceptions.auth.JWTInvalidTokenException;

@Service
public class JWTProvider {

    @Value("${JWT_SECRET}")
    private String jwt_secret;

    public String createToken(UUID id, String role){

        Algorithm algorithm = Algorithm.HMAC256(jwt_secret);

        return JWT.create()
                .withIssuer("todolist-api")
                .withSubject(id.toString())
                .withClaim("role", role)
                .withExpiresAt(Instant.now().plus(Duration.ofHours(2)))
                .sign(algorithm);
    }

    public DecodedJWT validateToken(String token){
        token = token.replace("Bearer ", "");

        Algorithm algorithm = Algorithm.HMAC256(jwt_secret);

        try{
            return JWT.require(algorithm)
            .build()
            .verify(token);

        }catch(JWTVerificationException e){
            throw new JWTInvalidTokenException();
        }
    }
    
}
