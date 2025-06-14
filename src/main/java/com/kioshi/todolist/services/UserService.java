package com.kioshi.todolist.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kioshi.todolist.dtos.task.DeleteAllTasksRequestDTO;
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
import com.kioshi.todolist.entities.UserEntity;
import com.kioshi.todolist.enums.Permission;
import com.kioshi.todolist.exceptions.user.InvalidCredentialsException;
import com.kioshi.todolist.exceptions.user.InvalidVerificationCodeException;
import com.kioshi.todolist.exceptions.user.UserAlreadyExistsException;
import com.kioshi.todolist.exceptions.user.UserNotFoundException;
import com.kioshi.todolist.repositories.UserRepository;
import com.kioshi.todolist.security.JWTProvider;
import com.kioshi.todolist.utils.VerificationCodeUtil;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private JWTProvider jwtProvider;

    @Autowired
    private VerificationCodeUtil verificationCodeUtil;

    @Autowired
    private TaskService taskService;

    @Autowired
    private EmailService emailService;

    // CREATE USER
    public CreateUserResponseDTO create(CreateUserRequestDTO createUserRequestDTO){
        this.userRepository.findByUsernameOrEmail(createUserRequestDTO.getUsername(), createUserRequestDTO.getEmail())
        .ifPresent((user) -> {
            throw new UserAlreadyExistsException();
        });

        String passwordEncoded = this.passwordEncoder.encode(createUserRequestDTO.getPassword());

        UserEntity user = UserEntity.builder()
        .name(createUserRequestDTO.getName())
        .username(createUserRequestDTO.getUsername())
        .password(passwordEncoded)
        .email(createUserRequestDTO.getEmail())
        .phoneNumber(createUserRequestDTO.getPhoneNumber())
        .verificationCode(null)
        .verified(false)
        .permission(Permission.USER)
        .build();

        UserEntity savedUser = userRepository.save(user);

        return new CreateUserResponseDTO(
            savedUser.getId(),
            savedUser.getName(),
            savedUser.getUsername(),
            savedUser.getEmail(),
            savedUser.getPhoneNumber()
        );
    }

    // LOGIN USER
    public LoginUserResponseDTO login(LoginUserRequestDTO loginUserRequestDTO) {
        UserEntity user = this.userRepository.findByEmail(loginUserRequestDTO.getEmail())
        .orElseThrow(() -> {
            throw new UserNotFoundException();
        });

        var passwordMatches = this.passwordEncoder.matches(loginUserRequestDTO.getPassword(), user.getPassword());

        if(!passwordMatches){
            throw new InvalidCredentialsException();
        }

        String role = "ROLE_" + user.getPermission().name();

        return new LoginUserResponseDTO(this.jwtProvider.createToken(user.getId(), role)); 
    }

    // GET PROFILE USER
    public GetProfileResponseDTO getProfile(GetProfileRequestDTO getProfileRequestDTO){
        UserEntity user = this.userRepository.findById(getProfileRequestDTO.getId())
        .orElseThrow(() -> {
            throw new UserNotFoundException();
        });

        return new GetProfileResponseDTO(
            user.getName(),
            user.getUsername(),
            user.getEmail(),
            user.getPhoneNumber(),
            user.getVerified(),
            user.getCreatedAt()
        );
    }


    // DELETE USER 
    @Transactional
    public DeleteUserResponseDTO delete(DeleteUserRequestDTO deleteUserRequestDTO){
        UserEntity user = this.userRepository.findById(deleteUserRequestDTO.getId())
        .orElseThrow(() -> {
            throw new UserNotFoundException();
        });

        this.taskService.deleteAllTasks(new DeleteAllTasksRequestDTO(user.getId()));
        this.userRepository.delete(user);
        
        return new DeleteUserResponseDTO(!this.userRepository.existsById(user.getId()));
    }

    //UPDATE USER
    @Transactional
    public UpdateUserResponseDTO update(UUID id, UpdateUserRequestDTO updateUserRequestDTO){
        UserEntity user = this.userRepository.findById(id)
        .orElseThrow(() -> {
            throw new UserNotFoundException();
        });

        if(updateUserRequestDTO.getName() != null){
            user.setName(updateUserRequestDTO.getName());
        }

        if(updateUserRequestDTO.getPhoneNumber() != null){
            user.setPhoneNumber(updateUserRequestDTO.getPhoneNumber());
        }

        UserEntity savedUser = this.userRepository.save(user);
        
        return new UpdateUserResponseDTO(
            savedUser.getName(),
            savedUser.getUsername(),
            savedUser.getEmail(),
            savedUser.getPhoneNumber()
        );
    }

    // SEND VERIFICATION CODE
    @Transactional
    public SendVerificationCodeResponseDTO sendVerificationCode(SendVerificationCodeRequestDTO sendVerificationCodeRequestDTO) throws MessagingException{

        UserEntity user = this.userRepository.findById(sendVerificationCodeRequestDTO.getId())
        .orElseThrow(() -> {
            throw new UserNotFoundException();
        });

        String verificationCode = this.verificationCodeUtil.generate();
        user.setVerificationCode(verificationCode);
        this.userRepository.save(user);

        String subject = "Código de Verificação ToDoList";
        String body = "Seu código de verificação é: " + verificationCode;

        this.emailService.sendEmail(user.getEmail(), subject, body);

        return new SendVerificationCodeResponseDTO(true);
    }

    // VERIFY ACCOUNT
    @Transactional
    public VerifyAccountResponseDTO verifyAccount(UUID id, VerifyAccountRequestDTO verifyAccountRequestDTO) throws MessagingException{

        UserEntity user = this.userRepository.findById(id)
        .orElseThrow(() -> {
            throw new UserNotFoundException(); 
        });

        if(user.getVerificationCode() == null || !user.getVerificationCode().equals(verifyAccountRequestDTO.getVerificationCode())){
            throw new InvalidVerificationCodeException();
        }

        user.setVerified(true);
        user.setVerificationCode(null);
        UserEntity savedUser = this.userRepository.save(user);

        String subject = "Conta Verificada";
        String body = "Sua conta foi verificada!";

        this.emailService.sendEmail(savedUser.getEmail(), subject, body);

        return new VerifyAccountResponseDTO(savedUser.getVerified());
    }

    // CHANGE PASSWORD
    public void changePassword(UUID id, ChangePasswordRequestDTO changePasswordRequestDTO){

        UserEntity user = this.userRepository.findById(id)
        .orElseThrow(() -> {
            throw new UserNotFoundException();
        });

        boolean passwordMatches = this.passwordEncoder.matches(changePasswordRequestDTO.getCurrentPassword(), user.getPassword());

        if(!passwordMatches){
            throw new InvalidCredentialsException();
        }

        String newPasswordEncoded = this.passwordEncoder.encode(changePasswordRequestDTO.getNewPassword());

        user.setPassword(newPasswordEncoded);
        this.userRepository.save(user);
    }

    // RESET PASSWORD
    public void resetPassword(ResetPasswordRequestDTO resetPasswordRequestDTO){

        UserEntity user = this.userRepository.findByEmail(resetPasswordRequestDTO.getEmail())
        .orElseThrow(() -> {
            throw new UserNotFoundException();
        });

        String newPasswordEncoded = this.passwordEncoder.encode(resetPasswordRequestDTO.getNewPassword());

        user.setPassword(newPasswordEncoded);
        this.userRepository.save(user);
    }
}


