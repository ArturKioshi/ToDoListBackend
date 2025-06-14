package com.kioshi.todolist.exceptions.task;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(){
        super("Task não encontrada");
    }
}
