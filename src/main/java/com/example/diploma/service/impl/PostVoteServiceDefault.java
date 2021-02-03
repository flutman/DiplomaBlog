package com.example.diploma.service.impl;

import com.example.diploma.enums.VoteType;
import com.example.diploma.exception.WrongPageException;
import com.example.diploma.model.Post;
import com.example.diploma.model.PostVote;
import com.example.diploma.model.User;
import com.example.diploma.repository.PostRepository;
import com.example.diploma.repository.PostVoteRepository;
import com.example.diploma.service.PostVoteService;
import com.example.diploma.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PostVoteServiceDefault implements PostVoteService {

    private final PostVoteRepository repository;
    private final UserService userService;
    private final PostRepository postRepository;

    @Override public boolean vote(VoteType vote, int postId) {
        User currentUser = userService.getCurrentUser();
        Post currentPost = postRepository.findById(postId).orElseThrow(
                () -> new WrongPageException("page not found")
        );
        Optional<PostVote> pv = repository.findPostVoteByPostAndUser(currentPost, currentUser);
        int voteRequested = vote.equals(VoteType.LIKE) ? 1 : -1;

        int userVote = pv.map(PostVote::getValue).orElse(0);

        //if already liked
        if (userVote == voteRequested) {
            return false;
        }

        PostVote postVote = pv.orElse(new PostVote());
        postVote.setPost(currentPost);
        postVote.setTime(LocalDateTime.now());
        postVote.setUser(currentUser);
        postVote.setValue(voteRequested);
        repository.save(postVote);

        return true;
    }

}
