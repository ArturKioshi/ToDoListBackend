package com.kioshi.todolist.integration.controllers;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

import com.github.javafaker.Faker;
import com.kioshi.todolist.dtos.user.ChangePasswordRequestDTO;
import com.kioshi.todolist.dtos.user.CreateUserRequestDTO;
import com.kioshi.todolist.dtos.user.CreateUserResponseDTO;
import com.kioshi.todolist.dtos.user.DeleteUserResponseDTO;
import com.kioshi.todolist.dtos.user.GetProfileResponseDTO;
import com.kioshi.todolist.dtos.user.LoginUserRequestDTO;
import com.kioshi.todolist.dtos.user.LoginUserResponseDTO;
import com.kioshi.todolist.dtos.user.ResetPasswordRequestDTO;
import com.kioshi.todolist.dtos.user.SendVerificationCodeResponseDTO;
import com.kioshi.todolist.dtos.user.UpdateUserRequestDTO;
import com.kioshi.todolist.dtos.user.UpdateUserResponseDTO;
import com.kioshi.todolist.dtos.user.VerifyAccountRequestDTO;
import com.kioshi.todolist.dtos.user.VerifyAccountResponseDTO;
import com.kioshi.todolist.entities.UserEntity;
import com.kioshi.todolist.exceptions.user.UserNotFoundException;
import com.kioshi.todolist.factories.UserFactories;
import com.kioshi.todolist.repositories.UserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class UserControllerTest {

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
    private PasswordEncoder passwordEncoder;

    private static Faker faker = new Faker();


    @BeforeEach
    void setUp(){
        testRestTemplate.getRestTemplate().setUriTemplateHandler(
                new DefaultUriBuilderFactory("http://localhost:" + port)
        );
    }

    @AfterEach
    void cleanUp() {
        this.userRepository.deleteAll();
    }

    @Test
    @DisplayName("it should be able create user")
    public void it_should_be_able_create_user() {
        
        CreateUserRequestDTO createUserRequestDTO = UserFactories.buildCreateUserRequestDTO();

        String url = "/api/v1/user/create";
        HttpEntity<CreateUserRequestDTO> request = new HttpEntity<>(createUserRequestDTO);

        ResponseEntity<CreateUserResponseDTO> response = testRestTemplate.exchange(
            url,
            HttpMethod.POST,
            request,
            CreateUserResponseDTO.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Status must be 201 (CREATED)");
        assertNotNull("The response body should not be null", response.getBody());

        CreateUserResponseDTO body = response.getBody();

        assertEquals(createUserRequestDTO.getName(), body.getName(), "Name must match");
        assertEquals(createUserRequestDTO.getUsername(), body.getUsername(), "Username must match");
        assertEquals(createUserRequestDTO.getEmail(), body.getEmail(), "Email must match");
        assertEquals(createUserRequestDTO.getPhoneNumber(), body.getPhoneNumber(), "Phone number must match");
    }

    @Test
    @DisplayName("it should be able to login user")
    public void it_should_be_able_to_login_user() {

        UserEntity user = UserFactories.createUser();
        String password = user.getPassword();
        String passwordEncoded = this.passwordEncoder.encode(password);
        user.setPassword(passwordEncoded);
        this.userRepository.save(user);

        LoginUserRequestDTO loginUserRequestDTO = new LoginUserRequestDTO(user.getEmail(), password);

        String url = "/api/v1/user/login";
        HttpEntity<LoginUserRequestDTO> request = new HttpEntity<>(loginUserRequestDTO);

        ResponseEntity<LoginUserResponseDTO> response = testRestTemplate.exchange(
            url,
            HttpMethod.POST,
            request,
            LoginUserResponseDTO.class     
            );

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode(), "Status must be 202 (ACCEPTED)");

        assertNotNull("The response body should not be null", response.getBody());

        LoginUserResponseDTO body = response.getBody();

        assertNotNull("Token should not be null", body.getToken());
        assertFalse(body.getToken().isEmpty(), "Token should not be empty");
    }

    @Test
    @DisplayName("it should be able get user profile")
    public void it_should_be_able_get_user_profile() {

        UserEntity user = UserFactories.createUser();
        String password = user.getPassword();
        String passwordEncoded = this.passwordEncoder.encode(password);
        user.setPassword(passwordEncoded);
        this.userRepository.save(user);

        LoginUserRequestDTO loginUserRequestDTO = new LoginUserRequestDTO(user.getEmail(), password);

        String url = "/api/v1/user/login";
        HttpEntity<LoginUserRequestDTO> requestLogin = new HttpEntity<>(loginUserRequestDTO);

        ResponseEntity<LoginUserResponseDTO> responseLogin = testRestTemplate.exchange(
            url,
            HttpMethod.POST,
            requestLogin,
            LoginUserResponseDTO.class     
            );

        assertEquals(HttpStatus.ACCEPTED, responseLogin.getStatusCode(), "Login should return 202 (ACCEPTED)");
        assertNotNull("Login response body should not be null", responseLogin.getBody());

        String tokenJWT = responseLogin.getBody().getToken();

        url = "/api/v1/user/profile";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tokenJWT);
        HttpEntity<Void> request = new HttpEntity<>(null, headers);

        ResponseEntity<GetProfileResponseDTO> response = testRestTemplate.exchange(
            url,
            HttpMethod.GET,
            request,
            GetProfileResponseDTO.class
            );

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status must be 200 (OK)");
        assertNotNull("The response body should not be null", response.getBody());

        GetProfileResponseDTO body = response.getBody();

        assertEquals(user.getName(), body.getName(), "Name must match");
        assertEquals(user.getUsername(), body.getUsername(), "Username must match");
        assertEquals(user.getEmail(), body.getEmail(), "Email must match");
        assertEquals(user.getPhoneNumber(), body.getPhoneNumber(), "PhoneNumber must match");
    }
    

    @Test
    @DisplayName("it should be able delete user")
    public void it_should_be_able_delete_user() {

        UserEntity user = UserFactories.createUser();
        String password = user.getPassword();
        String passwordEncoded = this.passwordEncoder.encode(password);
        user.setPassword(passwordEncoded);
        this.userRepository.save(user);

        LoginUserRequestDTO loginUserRequestDTO = new LoginUserRequestDTO(user.getEmail(), password);

        String url = "/api/v1/user/login";
        HttpEntity<LoginUserRequestDTO> requestLogin = new HttpEntity<>(loginUserRequestDTO);

        ResponseEntity<LoginUserResponseDTO> responseLogin = testRestTemplate.exchange(
            url,
            HttpMethod.POST,
            requestLogin,
            LoginUserResponseDTO.class     
            );

        assertEquals(HttpStatus.ACCEPTED, responseLogin.getStatusCode(), "Login should return 202 (ACCEPTED)");
        assertNotNull("Login response body should not be null", responseLogin.getBody());

        String tokenJWT = responseLogin.getBody().getToken();

        url = "/api/v1/user/delete";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tokenJWT);
        HttpEntity<Void> request = new HttpEntity<>(null, headers);

        ResponseEntity<DeleteUserResponseDTO> response = testRestTemplate.exchange(
            url, 
            HttpMethod.DELETE, 
            request, 
            DeleteUserResponseDTO.class
            );

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status must be 200 (OK)");
        assertNotNull("The response body should not be null", response.getBody());

        DeleteUserResponseDTO body = response.getBody();

        assertEquals(true, body.getDeleted());
    }

    @Test
    @DisplayName("it should be able update user")
    public void it_should_be_able_update_user() {

        UserEntity user = UserFactories.createUser();
        String password = user.getPassword();
        String passwordEncoded = this.passwordEncoder.encode(password);
        user.setPassword(passwordEncoded);
        this.userRepository.save(user);

        LoginUserRequestDTO loginUserRequestDTO = new LoginUserRequestDTO(user.getEmail(), password);

        String url = "/api/v1/user/login";
        HttpEntity<LoginUserRequestDTO> requestLogin = new HttpEntity<>(loginUserRequestDTO);

        ResponseEntity<LoginUserResponseDTO> responseLogin = testRestTemplate.exchange(
            url,
            HttpMethod.POST,
            requestLogin,
            LoginUserResponseDTO.class     
            );

        assertEquals(HttpStatus.ACCEPTED, responseLogin.getStatusCode(), "Login should return 202 (ACCEPTED)");
        assertNotNull("Login response body should not be null", responseLogin.getBody());

        String tokenJWT = responseLogin.getBody().getToken();

        url = "/api/v1/user/update";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tokenJWT);

        UpdateUserRequestDTO updateUserRequestDTO = UserFactories.buildUpdateUserRequestDTO();
        HttpEntity<UpdateUserRequestDTO> request = new HttpEntity<>(updateUserRequestDTO, headers);

        ResponseEntity<UpdateUserResponseDTO> response = testRestTemplate.exchange(
            url,
            HttpMethod.PATCH,
            request,
            UpdateUserResponseDTO.class    
        );

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status must be 200 (OK)");

        assertNotNull("The response body should not be null", response.getBody());

        UpdateUserResponseDTO body = response.getBody();

        assertEquals(updateUserRequestDTO.getName(), body.getName(), "Name must be the same");
        assertEquals(updateUserRequestDTO.getPhoneNumber(), body.getPhoneNumber(), "PhoneNumber must be the same");
        assertEquals(user.getEmail(), body.getEmail(), "Email must be the same as previous one");
        assertEquals(user.getUsername(), body.getUsername(), "Username must be the same as previous one");
    }

    @Test
    @DisplayName("it should be able verify account")
    public void it_should_be_able_verify_account() {
        
        UserEntity user = UserFactories.createUser();
        String password = user.getPassword();
        String passwordEncoded = this.passwordEncoder.encode(password);
        user.setPassword(passwordEncoded);
        this.userRepository.save(user);

        LoginUserRequestDTO loginUserRequestDTO = new LoginUserRequestDTO(user.getEmail(), password);

        String url = "/api/v1/user/login";
        HttpEntity<LoginUserRequestDTO> requestLogin = new HttpEntity<>(loginUserRequestDTO);

        ResponseEntity<LoginUserResponseDTO> responseLogin = testRestTemplate.exchange(
            url,
            HttpMethod.POST,
            requestLogin,
            LoginUserResponseDTO.class     
            );

        assertEquals(HttpStatus.ACCEPTED, responseLogin.getStatusCode(), "Login should return 202 (ACCEPTED)");
        assertNotNull("Login response body should not be null", responseLogin.getBody());

        String tokenJWT = responseLogin.getBody().getToken();

        url = "/api/v1/user/send-verification-code";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tokenJWT);
        HttpEntity<Void> requestSendVerificationCode = new HttpEntity<>(null, headers);

        ResponseEntity<SendVerificationCodeResponseDTO> responseSendVerificationCode = testRestTemplate.exchange(
            url,
            HttpMethod.POST,
            requestSendVerificationCode,
            SendVerificationCodeResponseDTO.class     
            );
        
        assertEquals(HttpStatus.OK, responseSendVerificationCode.getStatusCode(), "Send Verification Code should return 200 (OK)");
        assertNotNull("Send Verification Code response body should not be null", responseSendVerificationCode.getBody());
        assertEquals(true, responseSendVerificationCode.getBody().getSent(), "Send Verification Code: Sent should be true");

        user = this.userRepository.findById(user.getId())
        .orElseThrow(() -> {
            throw new UserNotFoundException();
        });

        url = "/api/v1/user/verify-account";
        VerifyAccountRequestDTO verifyAccountRequestDTO = new VerifyAccountRequestDTO(user.getVerificationCode());
        HttpEntity<VerifyAccountRequestDTO> request = new HttpEntity<>(verifyAccountRequestDTO, headers);

        ResponseEntity<VerifyAccountResponseDTO> response = testRestTemplate.exchange(
            url,
            HttpMethod.POST,
            request,
            VerifyAccountResponseDTO.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status must be 200 (OK)");
        assertNotNull("The response body should not be null", response.getBody());

        VerifyAccountResponseDTO body = response.getBody();

        assertEquals(true, body.getVerified(), "Verified must be true");
    }

    @Test
    @DisplayName("it should be able change user password")
    public void it_should_be_able_change_user_password() {
        
        UserEntity user = UserFactories.createUser();
        String password = user.getPassword();
        String passwordEncoded = this.passwordEncoder.encode(password);
        user.setPassword(passwordEncoded);
        this.userRepository.save(user);

        LoginUserRequestDTO loginUserRequestDTO = new LoginUserRequestDTO(user.getEmail(), password);

        String url = "/api/v1/user/login";
        HttpEntity<LoginUserRequestDTO> requestLogin = new HttpEntity<>(loginUserRequestDTO);

        ResponseEntity<LoginUserResponseDTO> responseLogin = testRestTemplate.exchange(
            url,
            HttpMethod.POST,
            requestLogin,
            LoginUserResponseDTO.class     
            );

        assertEquals(HttpStatus.ACCEPTED, responseLogin.getStatusCode(), "Login should return 202 (ACCEPTED)");
        assertNotNull("Login response body should not be null", responseLogin.getBody());

        String tokenJWT = responseLogin.getBody().getToken();

        url = "/api/v1/user/change-password";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tokenJWT);

        String newPassword = faker.internet().password(8, 16);
        ChangePasswordRequestDTO changePasswordRequestDTO = new ChangePasswordRequestDTO(password, newPassword);

        HttpEntity<ChangePasswordRequestDTO> request = new HttpEntity<>(changePasswordRequestDTO, headers);

        ResponseEntity<Void> response = testRestTemplate.exchange(
            url,
            HttpMethod.POST,
            request,
            Void.class    
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode(), "Status must be 204 (NO_CONTENT)");
        assertNull("The response body must be null", response.getBody());
    }

    @Test
    @DisplayName("it should be able reset user password")
    public void it_should_be_able_reset_user_password() {

        UserEntity user = UserFactories.createUser();
        String password = user.getPassword();
        String passwordEncoded = this.passwordEncoder.encode(password);
        user.setPassword(passwordEncoded);
        this.userRepository.save(user);

        String newPassword = faker.internet().password(8, 16);
        ResetPasswordRequestDTO resetPasswordRequestDTO = new ResetPasswordRequestDTO(user.getEmail(), newPassword);

        String url = "/api/v1/user/reset-password";
        HttpEntity<ResetPasswordRequestDTO> request = new HttpEntity<>(resetPasswordRequestDTO);

        ResponseEntity<Void> response = testRestTemplate.exchange(
            url,
            HttpMethod.POST,
            request,
            Void.class    
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode(), "Status must br 204 (NO_CONTENT)");
        assertNull("The response body must be null", response.getBody());
    }
}
