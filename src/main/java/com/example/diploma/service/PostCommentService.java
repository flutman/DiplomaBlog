package com.example.diploma.service;

import com.example.diploma.data.request.PostCommentRequest;
import com.example.diploma.exception.ApiError;
import com.example.diploma.exception.BadRequestException;
import com.example.diploma.model.Post;
import com.example.diploma.model.PostComment;
import com.example.diploma.repository.PostCommentRepository;
import com.example.diploma.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PostCommentService {

    private final PostRepository postRepository;
    private final PostCommentRepository commentRepository;
    private final UserService userService;

    public int writeComment(PostCommentRequest request) {
        if (request.getPostId() == 0) {
            throw new BadRequestException(new ApiError());
        }

        Post post = Optional.ofNullable(postRepository.findById(request.getPostId()))
                .orElseThrow(()-> new BadRequestException(new ApiError()));

        PostComment parentComment = new PostComment();
        if (request.getParentId() != 0) {
            parentComment = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new BadRequestException(new ApiError()));
        }

        PostComment newComment = new PostComment();
        if (parentComment.getId() != 0) {
            newComment.setParentPostComment(parentComment);
        }
        newComment.setPost(post);
        newComment.setText(request.getText());
        newComment.setTime(Instant.now());
        newComment.setUser(userService.getCurrentUser());
        newComment = commentRepository.save(newComment);

        return newComment.getId();
    }
}
