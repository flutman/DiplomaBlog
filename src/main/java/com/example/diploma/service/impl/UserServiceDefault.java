package com.example.diploma.service.impl;

import com.example.diploma.data.request.AuthenticationRequest;
import com.example.diploma.data.request.PasswordChangeRequest;
import com.example.diploma.data.request.RegisterRequest;
import com.example.diploma.data.response.LoginResponse;
import com.example.diploma.data.response.base.ResultResponse;
import com.example.diploma.data.response.type.PasswordError;
import com.example.diploma.data.response.type.PostError;
import com.example.diploma.data.response.type.RegisterErrorResponse;
import com.example.diploma.data.response.RegisterResponse;
import com.example.diploma.data.response.UserResponse;
import com.example.diploma.exception.ApiError;
import com.example.diploma.exception.BadRequestException;
import com.example.diploma.exception.PostErrorDto;
import com.example.diploma.model.CaptchaCode;
import com.example.diploma.model.User;
import com.example.diploma.repository.CaptchaCodeRepository;
import com.example.diploma.repository.UserRepository;
import com.example.diploma.security.SecurityUser;
import com.example.diploma.service.MailSender;
import com.example.diploma.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceDefault implements UserService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final CaptchaCodeRepository captchaCodeRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
    private final MailSender mailSender;

    //REGISTER
    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {
        RegisterResponse response = checkErrors(registerRequest);
        if (response.isResult()) {
            User newUser = registerNewUser(registerRequest);
            log.info("IN register - user: {} successfully register", newUser.getName());
        }

        return response;
    }

    private User registerNewUser(RegisterRequest registerRequest) {
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setIsModerator(0);
        user.setName(registerRequest.getName());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRegTime(LocalDateTime.now());

        return userRepository.save(user);
    }

    private RegisterResponse checkErrors(RegisterRequest registerRequest) {
        RegisterResponse response = new RegisterResponse();
        RegisterErrorResponse errors = new RegisterErrorResponse();

        if (checkEmail(registerRequest.getEmail())) {
            errors.setEmail("Этот e-mail уже зарегистрирован");
            response.setResult(false);
        }
        if (registerRequest.getName().isBlank()) {
            errors.setName("Имя указано неверно");
            response.setResult(false);
        }
        if (registerRequest.getPassword().length() < 6) {
            errors.setPassword("Пароль короче 6-ти символов");
            response.setResult(false);
        }
        if (!checkCaptcha(registerRequest.getCaptcha(), registerRequest.getCaptchaSecret())) {
            errors.setCaptcha("Код с картинки введен неверно");
            response.setResult(false);
        }
        response.setErrors(errors);
        return response;
    }

    private boolean checkCaptcha(String captcha, String captchaCodeSecret) {
        String code = captchaCodeRepository.findBySecretCode(captchaCodeSecret)
                .map(CaptchaCode::getCode).orElse("");
        return captcha.equals(code);
    }

    private boolean checkEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    //LOGIN
    @Override
    public LoginResponse login(AuthenticationRequest authenticationRequest) {
        String username = authenticationRequest.getEmail();

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, authenticationRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        SecurityUser securityUser = (SecurityUser) auth.getPrincipal();
        User currentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("username not found"));
        LoginResponse response = new LoginResponse();

        response.setResult(true);
        response.setUser(UserResponse.builder()
                .id(currentUser.getId())
                .name(currentUser.getName())
                .photo(currentUser.getPhoto())
                .email(currentUser.getEmail())
                .moderation(currentUser.getIsModerator() == 1)
                .moderationCount(0)
                .settings(true)
                .build()
        );

        log.info("Login user.email - {}", authenticationRequest.getEmail());

        return response;
    }

    @Override
    public LoginResponse checkUser(Principal principal) {
        LoginResponse response = new LoginResponse();
        if (principal == null) {
            return response;
        }

        User user = userRepository.findByEmail(principal.getName())
                .orElse(new User());

        if (user == null) {
            return response;
        }

        //TODO calculate moderationCount
        response.setResult(true);
        response.setUser(UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .photo(user.getPhoto())
                .email(user.getEmail())
                .moderation(false)
                .moderationCount(0)
                .settings(true)
                .build()
        );

        return response;
    }

    @Override
    public ResultResponse<PostError> restorePassword(String email) {
        //check User exist //TODO via checkEmail
        User user = userRepository.findByEmail(email).orElseThrow(
                ()-> new UsernameNotFoundException("user not found")
        );

        //sendEmail
        String activationCode = UUID.randomUUID().toString();
        String message = generateRestoreMessage(user.getName(), activationCode);

        if (!email.isBlank()) {
            user.setCode(activationCode);
            userRepository.save(user);
            mailSender.send(email, "[Test diploma] Restore password", message);
        }
        return new ResultResponse<>();
    }

    @Override
    public ResultResponse<PasswordError> changePassword(PasswordChangeRequest request) {
        //check request
        ResultResponse<PasswordError> response = new ResultResponse<>();
        PasswordError errors = new PasswordError();
        if (request.getPassword().length() < 6) {
            errors.addError("password","Пароль короче 6-ти символов");
            response.setResult(false);
            response.setErrors(errors);
            return response;
        }
        Optional<User> user = userRepository.findByCode(request.getCode());
        if (user.isEmpty()) {
            errors.addError("code","Ссылка для восстановления пароля устарела.\n" +
                    "<a href=\n" +
                    "\\\"/auth/restore\\\">Запросить ссылку снова</a>");
        }
        CaptchaCode code = captchaCodeRepository.findBySecretCode(request.getCaptchaSecret()).orElse(new CaptchaCode());
        if (!code.getCode().equals(request.getCaptcha())) {
            errors.addError("captcha","Код с картинки введен неверно");
        }

        if (errors.getErrors().size() != 0) {
            response.setResult(false);
            response.setErrors(errors);
            return response;
        }

        //if all is OK
        user.ifPresent(u -> {
            u.setPassword(passwordEncoder.encode(request.getPassword()));
            u.setCode(null);
            userRepository.save(u);
        });

        return new ResultResponse<>();
    }

    private String generateRestoreMessage(String userName, String activationCode){
        return String.format(
                "Привет, %s! \n Добро пожаловать на сайт DevPub. " +
                        "Для подтверждения перейди по ссылке: http://localhost:8080/login/change-password/%s",
                userName, activationCode
        );

    }

    @Override
    public LoginResponse logout() {
        //TODO обработать ошибку и переделать правильный ApiError
        LoginResponse response = new LoginResponse();
        response.setResult(true);
        return response;
    }
}
