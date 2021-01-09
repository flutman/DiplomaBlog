package com.example.diploma.service.impl;

import com.example.diploma.data.request.AuthenticationRequest;
import com.example.diploma.data.request.RegisterRequest;
import com.example.diploma.data.response.LoginResponse;
import com.example.diploma.data.response.type.RegisterErrorResponse;
import com.example.diploma.data.response.RegisterResponse;
import com.example.diploma.data.response.UserResponse;
import com.example.diploma.errors.ApiError;
import com.example.diploma.errors.BadRequestException;
import com.example.diploma.errors.PostErrorDto;
import com.example.diploma.model.CaptchaCode;
import com.example.diploma.model.User;
import com.example.diploma.repository.CaptchaCodeRepository;
import com.example.diploma.repository.UserRepository;
import com.example.diploma.security.SecurityUser;
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

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceDefault implements UserService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final CaptchaCodeRepository captchaCodeRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

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

//        if (!auth.isAuthenticated()) {
//            response.setResult(false);
//            return response;
//        }

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
    public LoginResponse logout() {
        //TODO обработать ошибку и переделать правильный ApiError
        if (SecurityContextHolder
                .getContext().getAuthentication().getAuthorities()
                .stream().map(Object::toString).findFirst().get().equals("ROLE_ANONYMOUS"))
            throw new BadRequestException(new ApiError(false, new PostErrorDto("error", "Bad")));
        LoginResponse response = new LoginResponse();
        response.setResult(true);
        return response;
    }
}
