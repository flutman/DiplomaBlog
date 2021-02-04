package com.example.diploma.service;

import com.example.diploma.data.request.ModerateRequest;
import com.example.diploma.data.request.NewPostRequest;
import com.example.diploma.data.response.PostResponse;
import com.example.diploma.data.response.PostWithCommentsResponse;
import com.example.diploma.data.response.base.ResultResponse;
import com.example.diploma.data.response.type.PostError;
import com.example.diploma.dto.CalendarDto;
import com.example.diploma.enums.ModerationStatus;
import com.example.diploma.enums.PostModerationStatus;
import com.example.diploma.model.Post;
import com.example.diploma.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;

public interface PostService {
    PostResponse getPosts(String mode, Pageable pageable);

    PostWithCommentsResponse getPost(Integer id);

    CalendarDto getCalendar(Integer year);

    PostResponse findPostsByQuery(String query, Pageable pageable);

    PostResponse findPostsByDate(String dateTime, Pageable pageable);

    PostResponse findPostsByTag(String tag, Pageable pageable);

    PostResponse findPostsForModeration(ModerationStatus status, Pageable pageable);

    PostResponse findMyPosts(PostModerationStatus status, Pageable pageable);

    @Transactional
    ResultResponse<PostError> addNewPost(NewPostRequest request, Errors errors);

    @Transactional
    ResultResponse<PostError> editPost(Integer id, NewPostRequest request, Errors errors);

    boolean moderatePost(ModerateRequest request);

    User checkCurrentUser();

    Post findPostById(Integer id);
}
