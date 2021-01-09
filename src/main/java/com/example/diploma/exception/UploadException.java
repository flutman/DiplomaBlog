package com.example.diploma.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class UploadException extends RuntimeException{
    private Map<String, Object> errors;

    public UploadException() {
        this.errors.put("image","Размер файла превышает допустимый размер");
    }

    public UploadException(Map<String,Object> errors) {
        this.errors = errors;
    }
}
