package com.kioshi.todolist.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kioshi.todolist.entities.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, UUID>{
    Optional<UserEntity> findByUsernameOrEmail(String username, String email);
    Optional<UserEntity> findByEmail(String email);
}
