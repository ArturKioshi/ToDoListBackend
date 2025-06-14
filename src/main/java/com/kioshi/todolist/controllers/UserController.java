package com.kioshi.todolist.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kioshi.todolist.dtos.user.ChangePasswordRequestDTO;
import com.kioshi.todolist.dtos.user.CreateUserRequestDTO;
import com.kioshi.todolist.dtos.user.CreateUserResponseDTO;
import com.kioshi.todolist.dtos.user.DeleteUserRequestDTO;
import com.kioshi.todolist.dtos.user.DeleteUserResponseDTO;
import com.kioshi.todolist.dtos.user.GetProfileRequestDTO;
import com.kioshi.todolist.dtos.user.GetProfileResponseDTO;
import com.kioshi.todolist.dtos.user.LoginUserRequestDTO;
import com.kioshi.todolist.dtos.user.LoginUserResponseDTO;
import com.kioshi.todolist.dtos.user.ResetPasswordRequestDTO;
import com.kioshi.todolist.dtos.user.SendVerificationCodeRequestDTO;
import com.kioshi.todolist.dtos.user.SendVerificationCodeResponseDTO;
import com.kioshi.todolist.dtos.user.UpdateUserRequestDTO;
import com.kioshi.todolist.dtos.user.UpdateUserResponseDTO;
import com.kioshi.todolist.dtos.user.VerifyAccountRequestDTO;
import com.kioshi.todolist.dtos.user.VerifyAccountResponseDTO;
import com.kioshi.todolist.exceptions.auth.JWTInvalidTokenException;
import com.kioshi.todolist.services.UserService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping("/create")
    public ResponseEntity<CreateUserResponseDTO> createUser(@Valid @RequestBody CreateUserRequestDTO createUserRequestDTO){
        CreateUserResponseDTO response = userService.create(createUserRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PostMapping("/login")
    public ResponseEntity<LoginUserResponseDTO> loginUser(@Valid @RequestBody LoginUserRequestDTO loginUserRequestDTO){
        LoginUserResponseDTO response = this.userService.login(loginUserRequestDTO);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }


    @GetMapping("/profile")
    public ResponseEntity<GetProfileResponseDTO> getProfileUser(HttpServletRequest request){
        Object idObject = request.getAttribute("user_id");

        if(idObject == null){
            throw new JWTInvalidTokenException();
        }

        UUID id = UUID.fromString(idObject.toString());

        GetProfileResponseDTO response = this.userService.getProfile(new GetProfileRequestDTO(id));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @DeleteMapping("/delete")
    public ResponseEntity<DeleteUserResponseDTO> deleteUser(HttpServletRequest request){
        Object idObject = request.getAttribute("user_id");

        if(idObject == null){
            throw new JWTInvalidTokenException();
        }

        UUID id = UUID.fromString(idObject.toString());

        DeleteUserResponseDTO response = this.userService.delete(new DeleteUserRequestDTO(id));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @PatchMapping("/update")
    public ResponseEntity<UpdateUserResponseDTO> updateUser(@Valid @RequestBody UpdateUserRequestDTO updateUserRequestDTO, HttpServletRequest request){
        Object idObject = request.getAttribute("user_id");

        if(idObject == null){
            throw new JWTInvalidTokenException();
        }

        UUID id = UUID.fromString(idObject.toString());

        UpdateUserResponseDTO response = this.userService.update(id, updateUserRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @PostMapping("/send-verification-code")
    public ResponseEntity<SendVerificationCodeResponseDTO> sendVerificationCode(HttpServletRequest request) throws MessagingException{
        Object idObject = request.getAttribute("user_id");

        if(idObject == null){
            throw new JWTInvalidTokenException();
        }

        UUID id = UUID.fromString(idObject.toString());

        SendVerificationCodeResponseDTO response = this.userService.sendVerificationCode(new SendVerificationCodeRequestDTO(id));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @PostMapping("/verify-account")
    public ResponseEntity<VerifyAccountResponseDTO> verifyAccount(@RequestBody VerifyAccountRequestDTO verifyAccountRequestDTO, HttpServletRequest request) throws MessagingException{
        Object idObject = request.getAttribute("user_id");

        if(idObject == null){
            throw new JWTInvalidTokenException();
        }

        UUID id = UUID.fromString(idObject.toString());

        VerifyAccountResponseDTO response = this.userService.verifyAccount(id, verifyAccountRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequestDTO changePasswordRequestDTO, HttpServletRequest request){
        Object idObject = request.getAttribute("user_id");

        if(idObject == null){
            throw new JWTInvalidTokenException();
        }

        UUID id = UUID.fromString(idObject.toString());

        this.userService.changePassword(id, changePasswordRequestDTO);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO resetPasswordRequestDTO){

        this.userService.resetPassword(resetPasswordRequestDTO);
        return ResponseEntity.noContent().build();
    }

}
