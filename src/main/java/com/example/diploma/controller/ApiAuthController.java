package com.example.diploma.controller;

import com.example.diploma.data.CheckResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    @GetMapping("/check")
    public ResponseEntity<CheckResponse> check(){
        return ResponseEntity.ok(new CheckResponse());
    }

}
