package com.kioshi.todolist.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kioshi.todolist.dtos.task.CreateTaskRequestDTO;
import com.kioshi.todolist.dtos.task.CreateTaskResponseDTO;
import com.kioshi.todolist.dtos.task.DeleteAllTasksRequestDTO;
import com.kioshi.todolist.dtos.task.DeleteAllTasksResponseDTO;
import com.kioshi.todolist.dtos.task.DeleteTaskRequestDTO;
import com.kioshi.todolist.dtos.task.DeleteTaskResponseDTO;
import com.kioshi.todolist.dtos.task.GetTasksRequestDTO;
import com.kioshi.todolist.dtos.task.GetTasksResponseDTO;
import com.kioshi.todolist.dtos.task.UpdateTaskRequestDTO;
import com.kioshi.todolist.dtos.task.UpdateTaskResponseDTO;
import com.kioshi.todolist.exceptions.auth.JWTInvalidTokenException;
import com.kioshi.todolist.services.TaskService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    // CREATE TASK
    @PostMapping("/create") 
    public ResponseEntity<CreateTaskResponseDTO> createTask(@Valid @RequestBody CreateTaskRequestDTO createTaskRequestDTO, HttpServletRequest request){
        Object idObject = request.getAttribute("user_id");
        if(idObject == null){
            throw new JWTInvalidTokenException();
        }

        UUID id = UUID.fromString(idObject.toString());

        CreateTaskResponseDTO response = this.taskService.create(createTaskRequestDTO, id);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    // GET ALL TASKS
    @GetMapping("/all")
    public ResponseEntity<GetTasksResponseDTO> getTasks(HttpServletRequest request){
        Object idObject = request.getAttribute("user_id");
        if(idObject == null){
            throw new JWTInvalidTokenException();
        }

        UUID UserId = UUID.fromString(idObject.toString());

        GetTasksResponseDTO response = this.taskService.getTasks(new GetTasksRequestDTO(UserId));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    // DELETE TASK
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<DeleteTaskResponseDTO> deleteTask(@PathVariable UUID id, HttpServletRequest request){

        Object idObject = request.getAttribute("user_id");
        if(idObject == null){
            throw new JWTInvalidTokenException();
        }

        UUID UserId = UUID.fromString(idObject.toString());

        DeleteTaskResponseDTO response = this.taskService.deleteTask(new DeleteTaskRequestDTO(UserId, id));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    // DELETE ALL TASKS FROM USER
    @DeleteMapping("/deleteAll")
    public ResponseEntity<DeleteAllTasksResponseDTO> deleteAllTasks(HttpServletRequest request){

        Object idObject = request.getAttribute("user_id");
        if(idObject == null){
            throw new JWTInvalidTokenException();
        }

        UUID userId = UUID.fromString(idObject.toString());

        DeleteAllTasksResponseDTO response = this.taskService.deleteAllTasks(new DeleteAllTasksRequestDTO(userId));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    
    //UPDATE TASK
    @PatchMapping("/update")
    public ResponseEntity<UpdateTaskResponseDTO> updateTask(@Valid @RequestBody UpdateTaskRequestDTO updateTaskRequestDTO){
        UpdateTaskResponseDTO response = this.taskService.update(updateTaskRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
}
