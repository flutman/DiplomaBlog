package com.example.diploma.service;

import com.example.diploma.data.request.PostCommentRequest;
import com.example.diploma.data.response.base.ResultResponse;
import com.example.diploma.exception.ApiError;
import com.example.diploma.exception.BadRequestException;
import com.example.diploma.exception.WrongPageException;
import com.example.diploma.model.Post;
import com.example.diploma.model.PostComment;
import com.example.diploma.repository.PostCommentRepository;
import com.example.diploma.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PostCommentService {

    private final PostRepository postRepository;
    private final PostCommentRepository commentRepository;
    private final UserService userService;

    public ResponseEntity<?> writeComment(PostCommentRequest request, Errors errors) {
        if (errors.hasErrors()) {
            ResultResponse<Map<String,String>> response = new ResultResponse<>(false);
            Map<String,String> commentErrors = new HashMap<>();
            errors.getFieldErrors().forEach(error -> commentErrors.put(error.getField(),error.getDefaultMessage()));
            response.setErrors(commentErrors);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        if (request.getPostId() == 0) {
            throw new BadRequestException(new ApiError());
        }

        Post post = postRepository.findById(request.getPostId()).orElseThrow(
                () -> new WrongPageException("page not found")
        );

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

        Map<String,String> response = Map.of("id", String.valueOf(newComment.getId()));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
