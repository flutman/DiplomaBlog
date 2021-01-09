package com.example.diploma.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class ApiError {
    private final boolean result = false;
    private Map<String, Object> errors;

    public ApiError(Map<String, Object> errors) {
        this.errors = errors;
    }
}
