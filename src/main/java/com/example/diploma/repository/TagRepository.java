package com.example.diploma.repository;

import com.example.diploma.model.Post;
import com.example.diploma.model.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TagRepository extends CrudRepository<Tag, Integer> {

    Tag findByNameIgnoreCase(String name);

    List<Tag> findByPosts(Post post);

    @Query("SELECT t FROM Tag t LEFT JOIN t.posts p WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' GROUP BY t.name")
    List<Tag> findTagOfPublishedPosts();

}
