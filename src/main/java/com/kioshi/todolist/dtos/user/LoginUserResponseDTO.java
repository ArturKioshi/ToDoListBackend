package com.kioshi.todolist.dtos.user;

import java.time.LocalDateTime;
import java.util.UUID;

import com.kioshi.todolist.enums.Permission;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserResponseDTO {
    
    private String token;
    private UUID id;
    private String name;
    private String username;
    private String email;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private Permission permission;
    private Boolean verified;
}
