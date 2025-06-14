package com.kioshi.todolist.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
import com.kioshi.todolist.entities.TaskEntity;
import com.kioshi.todolist.entities.UserEntity;
import com.kioshi.todolist.exceptions.auth.JWTInvalidTokenException;
import com.kioshi.todolist.exceptions.task.TaskNotFoundException;
import com.kioshi.todolist.exceptions.user.UserNotFoundException;
import com.kioshi.todolist.repositories.TaskRepository;
import com.kioshi.todolist.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    // CREATE TASK
    public CreateTaskResponseDTO create(CreateTaskRequestDTO createTaksRequestDTO, UUID id){

        UserEntity user = this.userRepository.findById(id)
        .orElseThrow(() -> {
            throw new UserNotFoundException();
        });

        TaskEntity task = TaskEntity.builder()
        .name(createTaksRequestDTO.getName())
        .content(createTaksRequestDTO.getContent())
        .completed(false)
        .userEntity(user)
        .build();

        TaskEntity savedTask = this.taskRepository.save(task);

        return new CreateTaskResponseDTO(
            savedTask.getName(),
            savedTask.getContent(),
            savedTask.getUserEntity().getId()
        );
    } 

    // GET TASKS
    public GetTasksResponseDTO getTasks(GetTasksRequestDTO getTasksDTO){

        this.userRepository.findById(getTasksDTO.getUserId())
        .orElseThrow(() ->{
            throw new UserNotFoundException();
        });

        List<TaskEntity> tasks = this.taskRepository.findByUserEntity_Id(getTasksDTO.getUserId());
        
        return new GetTasksResponseDTO(
            tasks
        );
    }

    @Transactional
    public DeleteTaskResponseDTO deleteTask(DeleteTaskRequestDTO deleteTaskRequestDTO){

        TaskEntity task = this.taskRepository.findById(deleteTaskRequestDTO.getId())
        .orElseThrow(() ->{
            throw new TaskNotFoundException();
        });

        if(task.getUserEntity().getId().equals(deleteTaskRequestDTO.getId())){
            throw new JWTInvalidTokenException();
        }

        this.taskRepository.delete(task);

        return new DeleteTaskResponseDTO(!this.taskRepository.existsById(task.getId()));
    }

    @Transactional
    public DeleteAllTasksResponseDTO deleteAllTasks(DeleteAllTasksRequestDTO deleteAllTasksRequestDTO){

        UserEntity user = this.userRepository.findById(deleteAllTasksRequestDTO.getUserId())
        .orElseThrow(() -> {
            throw new UserNotFoundException();
        });

        this.taskRepository.deleteByUserEntity_Id(user.getId());

        boolean deleted = this.taskRepository.findByUserEntity_Id(user.getId()).isEmpty();

        return new DeleteAllTasksResponseDTO(deleted);
    }

    @Transactional
    public UpdateTaskResponseDTO update(UpdateTaskRequestDTO updateTaskRequestDTO){
        
        TaskEntity task = this.taskRepository.findById(updateTaskRequestDTO.getId())
        .orElseThrow(() -> {
            throw new TaskNotFoundException();
        });

        if(updateTaskRequestDTO.getName() != null){
            task.setName(updateTaskRequestDTO.getName());
        }

        if(updateTaskRequestDTO.getContent() != null){
            task.setContent(updateTaskRequestDTO.getContent());
        }

        if(updateTaskRequestDTO.getCompleted() != null){
            task.setCompleted(updateTaskRequestDTO.getCompleted());
        }

        TaskEntity savedTask = this.taskRepository.save(task);

        return new UpdateTaskResponseDTO(
            savedTask.getId(),
            savedTask.getName(),
            savedTask.getContent(),
            savedTask.getCompleted()
        );  
    }
    
}
