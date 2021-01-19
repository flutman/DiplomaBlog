package com.example.diploma.controller;

import com.example.diploma.data.request.ProfileRequest;
import com.example.diploma.data.response.base.ResultResponse;
import com.example.diploma.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/profile")
@AllArgsConstructor
public class ProfileController {

    private final UserService userService;


    @PostMapping(value = "/my", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ResultResponse<?>> updateProfile(@Valid @RequestBody ProfileRequest request, Errors errors) {

        return ResponseEntity.ok(userService.editProfile(request, errors));
    }

    @PostMapping(value = "/my", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ResultResponse<?>> updateProfileWithPhoto(
            @RequestParam("photo") MultipartFile photo,
            @RequestParam("removePhoto") boolean removePhoto,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam(name = "password", required = false) String password
    ) {
        return ResponseEntity.ok(userService.updateProfileWithPhoto(photo, removePhoto, name, email, password));
    }
}
