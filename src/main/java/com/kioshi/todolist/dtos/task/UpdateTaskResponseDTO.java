package com.kioshi.todolist.dtos.task;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTaskResponseDTO {
    
    private UUID id;

    private String name;
    private String content;
    private boolean completed;
}
