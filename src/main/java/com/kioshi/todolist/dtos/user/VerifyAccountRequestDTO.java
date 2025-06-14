package com.kioshi.todolist.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyAccountRequestDTO {
    
    private String verificationCode;
}
