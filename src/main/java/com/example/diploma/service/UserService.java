package com.example.diploma.service;

import com.example.diploma.data.request.AuthenticationRequest;
import com.example.diploma.data.request.RegisterRequest;
import com.example.diploma.data.response.LoginResponse;
import com.example.diploma.data.response.RegisterResponse;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

public interface UserService {
    RegisterResponse register(RegisterRequest registerRequest);

    LoginResponse login(AuthenticationRequest authenticationRequest);

    LoginResponse logout();

    LoginResponse checkUser(Principal principal);
}
