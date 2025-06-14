package com.kioshi.todolist.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserResponseDTO {
    
    private String name;
    private String username;
    private String email;
    private String phoneNumber;
}
