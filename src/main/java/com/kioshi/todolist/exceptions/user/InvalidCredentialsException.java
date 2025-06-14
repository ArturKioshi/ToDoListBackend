package com.kioshi.todolist.exceptions.user;


public class InvalidCredentialsException extends RuntimeException{
    public InvalidCredentialsException(){
        super("E-mail ou senha incorreto");
    }
}
