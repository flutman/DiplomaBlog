package com.example.diploma.data.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileRequest {
    @NotBlank(message = "Имя указано неверно")
    private String name;
    @NotBlank(message = "E-mail указан неверно")
    private String email;
    @Size(min = 6, message = "Пароль короче 6 символов")
    private String password;
    private int removePhoto;

}
