package com.example.diploma.data;

import com.example.diploma.dto.CommentDto;
import com.example.diploma.dto.UserDto;
import com.example.diploma.model.Post;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Data
public class PostWithCommentsResponse {
    private long id;
    private long timestamp;
    private boolean active;
    private UserDto user;
    private String title;
    private String text;
    private long likeCount;
    private long dislikeCount;
    private int viewCount;
    private List<CommentDto> comments;
    private List<String> tags;

    public PostWithCommentsResponse(Post post, List<CommentDto> postComments, List<String> postTags){
        id = post.getPid();
        timestamp = post.getPtime().getEpochSecond();
        active = post.isActive();
        user = new UserDto(post.getUser());
        title = post.getTitle();
        text = post.getText();
        likeCount = post.getPostVotes().stream().filter(postVote -> postVote.getValue() > 0).count();
        dislikeCount = post.getPostVotes().stream().filter(postVote -> postVote.getValue() < 0).count();
        viewCount = post.getViewCount();
        comments = postComments;
        tags = postTags;
    }
}
