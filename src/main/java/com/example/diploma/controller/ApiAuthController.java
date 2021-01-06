package com.example.diploma.controller;

import com.example.diploma.data.request.AuthenticationRequest;
import com.example.diploma.data.request.RegisterRequest;
import com.example.diploma.data.response.*;
import com.example.diploma.repository.UserRepository;
import com.example.diploma.service.CaptchaService;
import com.example.diploma.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@Slf4j
public class ApiAuthController {

    private final CaptchaService captchaService;


    private final UserRepository userRepository;
    private final UserService userService;


    @GetMapping("/check")
    public ResponseEntity<LoginResponse> check() {
        return ResponseEntity.ok(userService.checkUser());
    }

    @GetMapping("/captcha")
    public ResponseEntity<CaptchaResponse> captcha() {
        return captchaService.getCaptchaResponse();
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest registerRequest){
        RegisterResponse response = userService.register(registerRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody AuthenticationRequest requestDto) {
        LoginResponse response = userService.login(requestDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/logout")
//    @PreAuthorize("hasAnyAuthority()")
    public ResponseEntity<LoginResponse> logout() {
        LoginResponse response = userService.logout();
        return ResponseEntity.ok(response);
    }


}
