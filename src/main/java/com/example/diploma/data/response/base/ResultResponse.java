package com.example.diploma.data.response.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResultResponse<T> {
    private boolean result = true;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private T errors;

    public ResultResponse(boolean result) {
        this.result = result;
    }
}
