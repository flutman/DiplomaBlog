package com.example.diploma.dto;

import lombok.Data;

@Data
public class PlainPostDto {
    private Integer id;
    private long timestamp;
    private UserDto user;
    private String title;
    private String announce;
    private Long likeCount;
    private Long dislikeCount;
    private Integer commentCount;
    private Integer viewCount;

}
