package com.example.diploma.errors;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WrongPageException extends RuntimeException {
    private String text;
}
