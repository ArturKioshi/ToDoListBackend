package com.kioshi.todolist.exceptions.user;

public class InvalidVerificationCodeException extends RuntimeException {
    public InvalidVerificationCodeException(){
        super("Código de Verificação Inválido");
    }
}
