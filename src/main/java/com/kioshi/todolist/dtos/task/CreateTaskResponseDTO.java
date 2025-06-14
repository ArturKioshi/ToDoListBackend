package com.kioshi.todolist.dtos.task;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateTaskResponseDTO {

    private String name;
    private String content;
    private UUID userId;   
}
