package com.example.diploma.data.response;

import com.example.diploma.dto.PlainPostDto;

import java.util.List;

public class PostResponse {
    private long count;
    private List<PlainPostDto> posts;

    public PostResponse() {

    }

    public long getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<PlainPostDto> getPosts() {
        return posts;
    }

    public void setPosts(List<PlainPostDto> posts) {
        count = posts.size();
        this.posts = posts;
    }

    public void setPosts(List<PlainPostDto> posts, long count) {
        this.count = count;
        this.posts = posts;
    }

}
