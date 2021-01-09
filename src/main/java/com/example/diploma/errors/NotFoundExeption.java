package com.example.diploma.errors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class NotFoundExeption extends RuntimeException{
    private final ApiError error;
}
