package com.kioshi.todolist.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kioshi.todolist.entities.TaskEntity;

public interface TaskRepository extends JpaRepository<TaskEntity, UUID>{
    List<TaskEntity> findByUserEntity_Id(UUID userId);
    void deleteByUserEntity_Id(UUID userId);
}
