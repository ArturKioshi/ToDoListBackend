package com.kioshi.todolist.integration.controllers;

import static org.junit.Assert.assertNotNull;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.kioshi.todolist.dtos.task.CreateTaskRequestDTO;
import com.kioshi.todolist.dtos.task.CreateTaskResponseDTO;
import com.kioshi.todolist.dtos.task.DeleteAllTasksResponseDTO;
import com.kioshi.todolist.dtos.task.DeleteTaskResponseDTO;
import com.kioshi.todolist.dtos.task.GetTasksResponseDTO;
import com.kioshi.todolist.dtos.task.UpdateTaskRequestDTO;
import com.kioshi.todolist.dtos.task.UpdateTaskResponseDTO;
import com.kioshi.todolist.entities.TaskEntity;
import com.kioshi.todolist.entities.UserEntity;
import com.kioshi.todolist.exceptions.user.UserNotFoundException;
import com.kioshi.todolist.factories.TaskFactories;
import com.kioshi.todolist.factories.UserFactories;
import com.kioshi.todolist.repositories.TaskRepository;
import com.kioshi.todolist.repositories.UserRepository;
import com.kioshi.todolist.utils.JWTGenerateTokenTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class TaskControllerTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.3")
        .withDatabaseName("testDB")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTGenerateTokenTest jwtGenerateTokenTest;

    private static String email = "johndoe@email.com";


    @BeforeEach
    void setUp(){
        testRestTemplate.getRestTemplate().setUriTemplateHandler(
                new DefaultUriBuilderFactory("http://localhost:" + port)
        );

        UserEntity user = UserFactories.createUser(
            "John Doe", 
            "john_doe", 
            "senha12345", 
            email,
            "18991111111"
        );

        this.userRepository.save(user);
    }

    @AfterEach
    void cleanUp() {
        this.taskRepository.deleteAll();
        this.userRepository.deleteAll();
    }


    @Test
    @DisplayName("it should be able create a task")
    public void it_should_be_able_create_a_task() {
        
        UserEntity user = this.userRepository.findByEmail(email)
        .orElseThrow(() -> {
            throw new UserNotFoundException();
        });

        assertNotNull("The user must not be null", user);

        CreateTaskRequestDTO createTaskRequestDTO = TaskFactories.buildCreateTaskRequestDTO();

        String url = "/api/v1/task/create";

        String tokenJWT = this.jwtGenerateTokenTest.createTokenTest(user.getId(), "ROLE_ " + user.getPermission());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tokenJWT);

        HttpEntity<CreateTaskRequestDTO> request = new HttpEntity<>(createTaskRequestDTO, headers);

        ResponseEntity<CreateTaskResponseDTO> response = testRestTemplate.exchange(
            url,
            HttpMethod.POST,
            request,
            CreateTaskResponseDTO.class  
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Status must be 201 (CREATED)");
        assertNotNull("The response body should not be null", response.getBody());

        CreateTaskResponseDTO body = response.getBody();

        assertEquals(createTaskRequestDTO.getName(), body.getName(), "Name must be the same");
        assertEquals(createTaskRequestDTO.getContent(), body.getContent(), "Content must be the same");
        assertEquals(user.getId(), body.getUserId(), "User Id must be the same");
    }

    @Test
    @DisplayName("it should be able get all tasks from a user")
    public void it_should_be_able_get_all_tasks_from_a_user() {

        UserEntity user = this.userRepository.findByEmail(email)
        .orElseThrow(() -> {
            throw new UserNotFoundException();
        });

        assertNotNull("The user must not be null", user);

        TaskEntity task1 = TaskFactories.createTask(user);
        this.taskRepository.save(task1);

        TaskEntity task2 = TaskFactories.createTask(user);
        this.taskRepository.save(task2);

        String url = "/api/v1/task/all";

        String tokenJWT = this.jwtGenerateTokenTest.createTokenTest(user.getId(), "ROLE_ " + user.getPermission());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tokenJWT);

        HttpEntity<Void> request = new HttpEntity<>(null, headers);

        ResponseEntity<GetTasksResponseDTO> response = testRestTemplate.exchange(
            url, 
            HttpMethod.GET,
            request,
            GetTasksResponseDTO.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status must be 200 (OK)");
        assertNotNull("The response body must not be null", response.getBody());

        GetTasksResponseDTO body = response.getBody();

        assertEquals(2, body.getTasks().size(), "The size of tasks list must be 2");
    }

    @Test
    @DisplayName("it should be able delete task")
    public void it_should_be_able_delete_task() {

        UserEntity user = this.userRepository.findByEmail(email)
        .orElseThrow(() -> {
            throw new UserNotFoundException();
        });

        assertNotNull("The user must not be null", user);

        TaskEntity task = TaskFactories.createTask(user);
        task = this.taskRepository.save(task);

        String url = "/api/v1/task/delete/" + task.getId();

        String tokenJWT = this.jwtGenerateTokenTest.createTokenTest(user.getId(), "ROLE_ " + user.getPermission());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tokenJWT);

        HttpEntity<Void> request = new HttpEntity<>(null, headers);

        ResponseEntity<DeleteTaskResponseDTO> response = testRestTemplate.exchange(
            url,
            HttpMethod.DELETE,
            request,
            DeleteTaskResponseDTO.class  
        );

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status must be 200 (OK)");
        assertNotNull("The response body must not be null", response.getBody());

        DeleteTaskResponseDTO body = response.getBody();

        assertEquals(true, body.getDeleted(), "Deleted must be true");
    }

    @Test
    @DisplayName("it should be able delete all tasks from a user")
    public void it_should_be_able_delete_all_tasks_from_a_user() {

        UserEntity user = this.userRepository.findByEmail(email)
        .orElseThrow(() -> {
            throw new UserNotFoundException();
        });

        assertNotNull("The user must not be null", user);

        TaskEntity task1 = TaskFactories.createTask(user);
        this.taskRepository.save(task1);

        TaskEntity task2 = TaskFactories.createTask(user);
        this.taskRepository.save(task2);

        String url = "/api/v1/task/deleteAll";

        String tokenJWT = this.jwtGenerateTokenTest.createTokenTest(user.getId(), "ROLE_ " + user.getPermission());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tokenJWT);

        HttpEntity<Void> request = new HttpEntity<>(null, headers);

        ResponseEntity<DeleteAllTasksResponseDTO> response = testRestTemplate.exchange(
            url,
            HttpMethod.DELETE,
            request,
            DeleteAllTasksResponseDTO.class  
        );

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status must be 200 (OK)");
        assertNotNull("The response body must not be null", response.getBody());

        DeleteAllTasksResponseDTO body = response.getBody();

        assertEquals(true, body.getDeleted(), "Deleted must be true");
    }

    @Test
    @DisplayName("it should be able update task")
    public void it_should_be_able_update_task() {

         UserEntity user = this.userRepository.findByEmail(email)
        .orElseThrow(() -> {
            throw new UserNotFoundException();
        });

        assertNotNull("The user must not be null", user);

        TaskEntity task = TaskFactories.createTask(user);
        task = this.taskRepository.save(task);

        String url = "/api/v1/task/update";

        UpdateTaskRequestDTO updateTaskRequestDTO = TaskFactories.buildUpdateTaskRequestDTO(task.getId());

        String tokenJWT = this.jwtGenerateTokenTest.createTokenTest(user.getId(), "ROLE_ " + user.getPermission());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tokenJWT);

        HttpEntity<UpdateTaskRequestDTO> request = new HttpEntity<>(updateTaskRequestDTO, headers);

        ResponseEntity<UpdateTaskResponseDTO> response = testRestTemplate.exchange(
            url,
            HttpMethod.PATCH,
            request,
            UpdateTaskResponseDTO.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status must be 200 (OK)");
        assertNotNull("The response body must not be null", response.getBody());

        UpdateTaskResponseDTO body = response.getBody();

        assertEquals(updateTaskRequestDTO.getName(), body.getName(), "Name must be the same");
        assertEquals(updateTaskRequestDTO.getContent(), body.getContent(), "Content must be the same");
        assertEquals(updateTaskRequestDTO.getCompleted(), body.getCompleted(), "Completed must be the same");
    }
}
