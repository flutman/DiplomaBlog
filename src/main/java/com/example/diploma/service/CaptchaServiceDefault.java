package com.example.diploma.service;

import com.example.diploma.data.response.CaptchaResponse;
import com.example.diploma.model.CaptchaCode;
import org.springframework.http.ResponseEntity;

public interface CaptchaServiceDefault {
    CaptchaCode getCaptcha();

    ResponseEntity<CaptchaResponse> getCaptchaResponse();
}
