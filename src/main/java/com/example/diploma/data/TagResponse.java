package com.example.diploma.data;

import com.example.diploma.dto.TagDto;

import java.util.List;

public class TagResponse {
    private List<TagDto> tags;

    public List<TagDto> getTags() {
        return tags;
    }

    public TagResponse(){

    }

    public void setTags(List<TagDto> tags) {
        this.tags = tags;
    }

}
