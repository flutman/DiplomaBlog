package com.example.diploma.controller;

import com.example.diploma.data.request.AuthenticationRequest;
import com.example.diploma.data.request.PasswordChangeRequest;
import com.example.diploma.data.request.RegisterRequest;
import com.example.diploma.data.request.RestorePasswordRequest;
import com.example.diploma.data.response.CaptchaResponse;
import com.example.diploma.data.response.LoginResponse;
import com.example.diploma.data.response.RegisterResponse;
import com.example.diploma.data.response.base.ResultResponse;
import com.example.diploma.data.response.type.PasswordError;
import com.example.diploma.data.response.type.PostError;
import com.example.diploma.service.impl.CaptchaService;
import com.example.diploma.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@Slf4j
public class ApiAuthController {

    private final CaptchaService captchaService;
    private final UserService userService;

    @GetMapping("/check")
    public ResponseEntity<LoginResponse> check(Principal principal) {
        return ResponseEntity.ok(userService.checkUser(principal));
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
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<LoginResponse> logout() {
        SecurityContextHolder.clearContext();
        LoginResponse response = userService.logout();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/restore")
    public ResponseEntity<ResultResponse<PostError>> restorePassword(@RequestBody RestorePasswordRequest request) {
        ResultResponse<PostError> response = userService.restorePassword(request.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/password")
    public ResponseEntity<ResultResponse<PasswordError>> changePassword(@RequestBody PasswordChangeRequest request) {
        ResultResponse<PasswordError> response = userService.changePassword(request);
        return ResponseEntity.ok(response);
    }

}
