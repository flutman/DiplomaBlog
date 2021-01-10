package com.example.diploma.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class BadRequestException extends RuntimeException {
    private ApiError error = new ApiError();

    public BadRequestException () {
    }

    public BadRequestException(ApiError error) {
        this.error = error;
    }
}
