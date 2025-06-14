package com.kioshi.todolist.exceptions.user;

public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException() {
        super("Usuário já existe");
    }
}
