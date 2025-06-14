package com.kioshi.todolist.dtos.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteAllTasksResponseDTO {
    
    private boolean deleted;
}
