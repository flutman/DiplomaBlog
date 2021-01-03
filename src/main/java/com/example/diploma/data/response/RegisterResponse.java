package com.example.diploma.data.response;

import com.example.diploma.data.response.base.Response;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RegisterResponse extends Response {
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private RegisterErrorResponse errors;
}
