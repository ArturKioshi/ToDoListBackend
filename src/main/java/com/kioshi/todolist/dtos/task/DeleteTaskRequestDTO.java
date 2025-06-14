package com.kioshi.todolist.dtos.task;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteTaskRequestDTO {
    
    private UUID UserId;
    private UUID id;
}
