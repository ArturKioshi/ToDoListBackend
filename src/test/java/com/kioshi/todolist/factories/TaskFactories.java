package com.kioshi.todolist.factories;

import java.util.UUID;

import com.github.javafaker.Faker;
import com.kioshi.todolist.dtos.task.CreateTaskRequestDTO;
import com.kioshi.todolist.dtos.task.UpdateTaskRequestDTO;
import com.kioshi.todolist.entities.TaskEntity;
import com.kioshi.todolist.entities.UserEntity;

public class TaskFactories {
    
    private static Faker faker = new Faker();


    public static TaskEntity createTask(UserEntity user) {
        return TaskEntity.builder()
        .name(faker.lorem().sentence(3, 5))
        .content(faker.lorem().paragraph())
        .completed(false)
        .userEntity(user)
        .build();
    }

    public static CreateTaskRequestDTO buildCreateTaskRequestDTO() {
        return new CreateTaskRequestDTO(
            faker.lorem().sentence(3, 5),
            faker.lorem().paragraph()
        );
    }

    public static UpdateTaskRequestDTO buildUpdateTaskRequestDTO(UUID id) {
        return new UpdateTaskRequestDTO(
            id, 
            faker.lorem().sentence(3, 5),
            faker.lorem().paragraph(),
            true
        );
    }
}
