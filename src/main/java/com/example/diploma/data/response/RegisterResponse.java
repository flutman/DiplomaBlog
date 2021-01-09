package com.example.diploma.data.response;

import com.example.diploma.data.response.base.ResultResponse;
import com.example.diploma.data.response.type.RegisterErrorResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class RegisterResponse extends ResultResponse {
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private RegisterErrorResponse errors;
}
