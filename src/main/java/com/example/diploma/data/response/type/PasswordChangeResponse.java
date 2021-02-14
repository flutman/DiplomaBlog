package com.example.diploma.data.response.type;

import lombok.Data;

import java.util.HashMap;

@Data
public class PasswordChangeResponse {
    private HashMap<String, Object> errors = new HashMap<>();

    public void addError(String field, String message) {
        errors.put(field, message);
    }
}
