package com.example.diploma.data.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LoginResponse {
    private boolean result;
    private UserResponse user;
    private String token; //TODO возможно нужно перенести в другое место
}
