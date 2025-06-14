package com.kioshi.todolist.dtos.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateUserRequestDTO {

    @NotBlank(message = "O nome é obrigatório!")
    private String name;

    @NotBlank(message = "O username é obrigatório!")
    @Pattern(regexp = "\\S+", message = "O username não deve conter espaços!")
    private String username;

    @NotBlank(message = "A senha é obrigatória!")
    @Size(min = 8, message = "A senha deve conter no mínimo 8 caracteres!")
    private String password;

    @NotBlank(message = "O e-mail é obrigatório!")
    @Email(message = "O campo (email) deve conter um e-mail válido!")
    private String email;

    @NotBlank(message = "O número de telefone é obrigatório!")
    @Pattern(regexp = "\\d{9,15}", message = "O número de telefone deve conter apenas dígitos (mín. 9, máx. 15).")
    private String phoneNumber;
}
