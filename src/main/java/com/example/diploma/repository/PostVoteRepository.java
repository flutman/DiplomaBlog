package com.example.diploma.repository;

import com.example.diploma.model.Post;
import com.example.diploma.model.PostVote;
import com.example.diploma.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PostVoteRepository extends CrudRepository<PostVote, Integer> {

    Optional<PostVote> findPostVoteByPostAndUser(Post post, User user);
}
