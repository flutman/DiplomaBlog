package com.example.diploma.controller;

import com.example.diploma.data.response.PostResponse;
import com.example.diploma.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
@RequestMapping("/api/post")
public class ApiPostController {

    private final PostService postService;

    @GetMapping("/{id}")
    public ResponseEntity<?> showPostById(
            @PathVariable String id
    ) {
            return ResponseEntity.ok(postService.getPost(id));
    }

    @GetMapping("")
    public ResponseEntity<PostResponse> getAllPosts(
            @RequestParam Integer offset,
            @RequestParam Integer limit,
            @RequestParam String mode
    ) {
        PostResponse response = postService.getPosts(mode, PageRequest.of((int) offset / limit, limit));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<PostResponse> findPostsByQuery(
            @RequestParam Integer offset,
            @RequestParam Integer limit,
            @RequestParam String query
    ) {
        PostResponse response = postService.findPostsByQuery(query, PageRequest.of((int) offset / limit, limit));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/byDate")
    public ResponseEntity<PostResponse> findPostsByDate(
            @RequestParam Integer offset,
            @RequestParam Integer limit,
            @RequestParam String date
    ) {
        PostResponse response = postService.findPostsByDate(date, PageRequest.of((int) offset / limit, limit));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/byTag")
    public ResponseEntity<PostResponse> findPostsByTag(
            @RequestParam Integer offset,
            @RequestParam Integer limit,
            @RequestParam String tag
    ) {
        PostResponse response = postService.findPostsByTag(tag, PageRequest.of((int) offset / limit, limit));
        return ResponseEntity.ok(response);
    }

}
