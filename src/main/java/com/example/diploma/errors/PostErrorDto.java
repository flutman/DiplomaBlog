package com.example.diploma.errors;

import lombok.Data;

@Data
public class PostErrorDto {
    String title;
    String text;

    public PostErrorDto(String title, String text) {
        this.title = title;
        this.text = text;
    }
}
