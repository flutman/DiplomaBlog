package com.example.diploma.service;

import com.example.diploma.data.request.PostCommentRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;

public interface PostCommentService {
    ResponseEntity<?> writeComment(PostCommentRequest request, Errors errors);
}
