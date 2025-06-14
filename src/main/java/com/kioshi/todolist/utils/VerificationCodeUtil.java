package com.kioshi.todolist.utils;

import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class VerificationCodeUtil {

    private final Random random = new Random();
    
    public String generate(){

        int code = 10000 + this.random.nextInt(90000); // Gera número entre 10000 e 99999
        return String.valueOf(code);
    }

}
