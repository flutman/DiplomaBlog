package com.example.diploma.data.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NewPostRequest {
    private long timestamp;
    private int active;
    @NotBlank(message = "Заголовок поста не установлен")
    @Size(min = 5, message = "Заголовок менее 5 символов")
    private String title;
    private List<String> tags;
    @NotBlank(message = "Текст поста не установлен")
    @Size(min = 50, message = "Текст поста менее 50 символов")
    private String text;
}
