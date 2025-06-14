package com.kioshi.todolist.dtos.user;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequestDTO {

    private String currentPassword;

    @Size(min = 8, message = "A senha deve conter no mínimo 8 caracteres!")
    private String newPassword;
    
}
