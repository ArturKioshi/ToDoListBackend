package com.kioshi.todolist.dtos.task;

import java.util.List;

import com.kioshi.todolist.entities.TaskEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetTasksResponseDTO {
    
    private List<TaskEntity> tasks;
}
