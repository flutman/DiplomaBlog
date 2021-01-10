package com.example.diploma.data.response.type;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class RegisterErrorResponse {

    private String email;
    private String name;
    @Size(min = 6)
    private String password;
    private String captcha;

}
