package com.kioshi.todolist.dtos.task;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTaskRequestDTO {
    
    @NotBlank(message = "O nome é obrigatório")
    private String name;

    @NotBlank(message = "O conteúdo é obrigatório")
    private String content;
}
