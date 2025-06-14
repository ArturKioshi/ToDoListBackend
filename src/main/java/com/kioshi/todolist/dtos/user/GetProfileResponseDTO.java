package com.kioshi.todolist.dtos.user;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetProfileResponseDTO {

    private String name;
    private String username;
    private String email;
    private String phoneNumber;
    private Boolean verified;
    private LocalDateTime createdAt;
    
}
