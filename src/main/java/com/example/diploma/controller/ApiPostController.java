package com.example.diploma.controller;

import com.example.diploma.data.request.NewPostRequest;
import com.example.diploma.data.response.PostResponse;
import com.example.diploma.data.response.base.ResultResponse;
import com.example.diploma.data.response.type.PostError;
import com.example.diploma.enums.ModerationStatus;
import com.example.diploma.enums.PostModerationStatus;
import com.example.diploma.enums.VoteType;
import com.example.diploma.service.PostService;
import com.example.diploma.service.PostVoteService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@Controller
@AllArgsConstructor
@RequestMapping("/api/post")
public class ApiPostController {

    private final PostService postService;
    private final PostVoteService postVoteService;

    @GetMapping("/{id}")
    public ResponseEntity<?> showPostById(
            @PathVariable int id
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

    @PostMapping("")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ResultResponse<PostError>> addNewPost(
            @RequestBody @Valid NewPostRequest request,
            Errors errors
    ) {
        ResultResponse<PostError> response = postService.addNewPost(request, errors);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ResultResponse<PostError>> editPost(
            @PathVariable int id,
            @RequestBody @Valid NewPostRequest request,
            Errors errors
    ) {
        ResultResponse<PostError> response = postService.editPost(id, request, errors);
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

    @GetMapping("/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<PostResponse> findPostsForModeration(
            @RequestParam Integer offset,
            @RequestParam Integer limit,
            @RequestParam ModerationStatus status
    ) {
        PostResponse response = postService.findPostsForModeration(status, PageRequest.of((int) offset / limit, limit));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<PostResponse> findMyPosts(
            @RequestParam Integer offset,
            @RequestParam Integer limit,
            @RequestParam PostModerationStatus status
    ) {
        PostResponse response = postService.findMyPosts(status, PageRequest.of((int) offset / limit, limit));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{vote}")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ResultResponse<?>> vote(@PathVariable VoteType vote, @RequestBody Map<String, Integer> body) {
        boolean result = postVoteService.vote(vote, body.getOrDefault("post_id", 0));
        ResultResponse<?> response = new ResultResponse<>(result);
        return ResponseEntity.ok(response);
    }

//    @PostMapping("/dislike")
//    @PreAuthorize("hasAuthority('user:write')")
//    public ResponseEntity<ResultResponse<?>> dislikeVote(@RequestBody Map<String, Integer> body) {
//        boolean result = postVoteService.dislikePost(body.getOrDefault("post_id", 0));
//        ResultResponse<?> response = new ResultResponse<>(result);
//        return ResponseEntity.ok(response);
//    }

}
