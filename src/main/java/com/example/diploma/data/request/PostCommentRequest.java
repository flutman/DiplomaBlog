package com.example.diploma.data.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PostCommentRequest {
    @JsonProperty("parent_id")
    private int parentId;
    @JsonProperty("post_id")
    private int postId;
    @NotBlank(message = "Текст комментария не задан или слишком короткий")
    private String text;
}
