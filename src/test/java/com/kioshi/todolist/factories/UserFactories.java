package com.kioshi.todolist.factories;

import com.github.javafaker.Faker;
import com.kioshi.todolist.dtos.user.CreateUserRequestDTO;
import com.kioshi.todolist.dtos.user.UpdateUserRequestDTO;
import com.kioshi.todolist.entities.UserEntity;
import com.kioshi.todolist.enums.Permission;

public class UserFactories {

    private static Faker faker = new Faker();


    public static UserEntity createUser(){
        String rawUsername = faker.name().username();
        String cleanUsername = rawUsername.replaceAll("[^a-zA-Z0-9_]", "");

        return UserEntity.builder()
        .name(faker.name().fullName())
        .username(cleanUsername)
        .password(faker.internet().password(8,16))
        .email(faker.internet().emailAddress())
        .phoneNumber(faker.number().digits(11))
        .verificationCode(null)
        .verified(false)
        .permission(Permission.USER)
        .build();
    }

    public static UserEntity createUser(String name, String username, String password, String email, String phoneNumber){
        return UserEntity.builder()
        .name(name)
        .username(username)
        .password(password)
        .email(email)
        .phoneNumber(phoneNumber)
        .verificationCode(null)
        .verified(false)
        .permission(Permission.USER)
        .build();
    }

    
    public static CreateUserRequestDTO buildCreateUserRequestDTO(){
        String rawUsername = faker.name().username();
        String cleanUsername = rawUsername.replaceAll("[^a-zA-Z0-9_]", "");

        return new CreateUserRequestDTO(
            faker.name().fullName(),
            cleanUsername,
            faker.internet().password(8, 16),
            faker.internet().emailAddress(),
            faker.number().digits(11)
        );
    }

    public static UpdateUserRequestDTO buildUpdateUserRequestDTO() {
        return new UpdateUserRequestDTO(
            faker.name().fullName(),
            faker.number().digits(11)
        );
    } 

}
