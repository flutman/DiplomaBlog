package com.example.diploma.controller;

import com.example.diploma.data.request.PostCommentRequest;
import com.example.diploma.data.response.base.ResultResponse;
import com.example.diploma.service.PostCommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class PostCommentController {

    private final PostCommentService postCommentService;

    @PostMapping("/api/comment")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<?> writeComment(@RequestBody PostCommentRequest request, Errors errors){
        if (errors.hasErrors()) {
            ResultResponse<Map<String,String>> response = new ResultResponse<>(false);
            Map<String,String> commentErrors = new HashMap<>();
            errors.getFieldErrors().forEach(error -> commentErrors.put(error.getField(),error.getDefaultMessage()));
            response.setErrors(commentErrors);
            return ResponseEntity.ok(response);
        }
        int result = postCommentService.writeComment(request);
        return ResponseEntity.ok(Map.of("id", result));
    }
}
