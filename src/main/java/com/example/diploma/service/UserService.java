package com.example.diploma.service;

import com.example.diploma.data.request.AuthenticationRequest;
import com.example.diploma.data.request.PasswordChangeRequest;
import com.example.diploma.data.request.ProfileRequest;
import com.example.diploma.data.request.RegisterRequest;
import com.example.diploma.data.response.LoginResponse;
import com.example.diploma.data.response.RegisterResponse;
import com.example.diploma.data.response.base.ResultResponse;
import com.example.diploma.data.response.type.PasswordError;
import com.example.diploma.data.response.type.PostError;
import com.example.diploma.model.User;
import com.example.diploma.model.enums.Permission;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Map;

public interface UserService {
    RegisterResponse register(RegisterRequest registerRequest);

    LoginResponse login(AuthenticationRequest authenticationRequest);

    LoginResponse logout();

    LoginResponse checkUser(Principal principal);

    ResultResponse<PostError> restorePassword(String email);

    ResultResponse<PasswordError> changePassword(PasswordChangeRequest request);

    User getCurrentUser();

    boolean getUserPermission(Permission permission);

    ResultResponse<Map<String,String>> editProfile(ProfileRequest request, Errors errors);

    ResultResponse<Map<String, String>> updateProfileWithPhoto(MultipartFile photo,
                                                          Boolean removePhoto,
                                                          String name,
                                                          String email,
                                                          String password);
}
