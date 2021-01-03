package com.example.diploma.errors;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiError {
    boolean result;
    PostErrorDto errors;
}
