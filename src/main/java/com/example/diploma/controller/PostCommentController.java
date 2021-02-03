package com.example.diploma.controller;

import com.example.diploma.data.request.PostCommentRequest;
import com.example.diploma.service.PostCommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class PostCommentController {

    private final PostCommentService postCommentService;

    @PostMapping("/api/comment")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<?> writeComment(@RequestBody PostCommentRequest request, Errors errors){
        return postCommentService.writeComment(request, errors);
    }
}
