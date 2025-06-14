package com.kioshi.todolist.exceptions.auth;

public class JWTInvalidTokenException extends RuntimeException{

    public JWTInvalidTokenException() {
        super("Token inválido ou expirado");
    }
    
}
