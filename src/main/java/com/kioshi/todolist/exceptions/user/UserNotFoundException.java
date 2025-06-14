package com.kioshi.todolist.exceptions.user;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(){
        super("Usuário não encontrado");
    }
}
