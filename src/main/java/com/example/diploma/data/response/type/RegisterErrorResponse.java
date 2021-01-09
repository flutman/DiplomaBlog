package com.example.diploma.data.response.type;

import lombok.Data;

@Data
public class RegisterErrorResponse {
    private String email;
    private String name;
    private String password;
    private String captcha;

}
