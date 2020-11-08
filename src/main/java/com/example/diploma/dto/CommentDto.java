package com.example.diploma.dto;

import com.example.diploma.model.PostComment;
import lombok.Data;

@Data
public class CommentDto {
    private long id;
    private long timestamp;
    private String text;
    private UserDto user;


    public CommentDto(PostComment postComment) {
        id = postComment.getId();
        timestamp = postComment.getTime().getEpochSecond();
        text = postComment.getText();
        user = new UserDto(postComment.getUser());
    }
}
