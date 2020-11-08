package com.example.diploma.repository;

import com.example.diploma.model.Post;
import com.example.diploma.model.Tag;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TagRepository extends CrudRepository<Tag,Integer> {
    List<String> findByPosts(Post post);
}
