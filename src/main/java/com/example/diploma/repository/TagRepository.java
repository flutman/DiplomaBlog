package com.example.diploma.repository;

import com.example.diploma.model.Post;
import com.example.diploma.model.Tag;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TagRepository extends CrudRepository<Tag, Integer> {

    Tag findByNameIgnoreCase(String name);

    List<Tag> findByPosts(Post post);

}
