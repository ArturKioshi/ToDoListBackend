package com.kioshi.todolist.dtos.user;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateUserResponseDTO {

    private UUID id;
    private String name;
    private String username;
    private String email;
    private String phoneNumber;
}
