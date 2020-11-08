package com.example.diploma.repository;

import com.example.diploma.model.Post;
import com.example.diploma.model.PostComment;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PostCommentRepository extends CrudRepository<PostComment, Integer> {
    List<PostComment> findByPost(Post post);
}
