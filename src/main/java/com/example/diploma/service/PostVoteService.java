package com.example.diploma.service;

import com.example.diploma.model.Post;
import com.example.diploma.model.PostVote;
import com.example.diploma.model.User;
import com.example.diploma.repository.PostRepository;
import com.example.diploma.repository.PostVoteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PostVoteService {

    private final PostVoteRepository repository;
    private final UserService userService;
    private final PostRepository postRepository;

    public boolean likePost(int postId) {
        User currentUser = userService.getCurrentUser();
        Post currentPost = postRepository.findById(postId);
        Optional<PostVote> pv = repository.findPostVoteByPostAndUser(currentPost, currentUser);

        int userVote = pv.map(PostVote::getValue).orElse(0);

        //if already liked
        if (userVote > 0) {
            return false;
        }

        PostVote postVote = pv.orElse(new PostVote());
        postVote.setPost(currentPost);
        postVote.setTime(LocalDateTime.now());
        postVote.setUser(currentUser);
        postVote.setValue(1);
        repository.save(postVote);

        return true;
    }

    public boolean dislikePost(int postId) {
        User currentUser = userService.getCurrentUser();
        Post currentPost = postRepository.findById(postId);
        Optional<PostVote> pv = repository.findPostVoteByPostAndUser(currentPost, currentUser);

        int userVote = pv.map(PostVote::getValue).orElse(0);

        //if already liked
        if (userVote < 0) {
            return false;
        }

        PostVote postVote = pv.orElse(new PostVote());
        postVote.setPost(currentPost);
        postVote.setTime(LocalDateTime.now());
        postVote.setUser(currentUser);
        postVote.setValue(-1);
        repository.save(postVote);

        return true;
    }

}
