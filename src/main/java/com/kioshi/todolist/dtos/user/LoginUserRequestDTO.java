package com.kioshi.todolist.dtos.user;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserRequestDTO {
    
    @Email(message = "O campo (email) deve conter um e-mail válido!")
    private String email;
    
    private String password;
}
