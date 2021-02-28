package com.example.diploma.service;

import com.example.diploma.data.response.TagResponse;
import com.example.diploma.model.Tag;

import java.util.List;

public interface TagService {

    TagResponse getTags();

    Tag saveNewTag(String name);

    void deletePrevTags(List<Tag> tagList);

    Tag findTagByName(String name);
}
