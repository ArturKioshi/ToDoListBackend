package com.kioshi.todolist.utils;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@Component
public class JWTGenerateTokenTest {
 
    @Value("${JWT_SECRET}")
    private String jwt_secret;

    public String createTokenTest(UUID id, String role){

        Algorithm algorithm = Algorithm.HMAC256(jwt_secret);

        return JWT.create()
                .withIssuer("todolist-api-test")
                .withSubject(id.toString())
                .withClaim("role", role)
                .withExpiresAt(Instant.now().plus(Duration.ofHours(2)))
                .sign(algorithm);
    }
}
