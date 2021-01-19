package com.example.diploma.service;

import com.example.diploma.data.request.ModerateRequest;
import com.example.diploma.data.request.NewPostRequest;
import com.example.diploma.data.response.PostResponse;
import com.example.diploma.data.response.PostWithCommentsResponse;
import com.example.diploma.data.response.base.ResultResponse;
import com.example.diploma.data.response.type.PostError;
import com.example.diploma.dto.CalendarDto;
import com.example.diploma.dto.CommentDto;
import com.example.diploma.dto.PlainPostDto;
import com.example.diploma.enums.ModerationStatus;
import com.example.diploma.enums.PostModerationStatus;
import com.example.diploma.exception.ApiError;
import com.example.diploma.exception.NotFoundException;
import com.example.diploma.exception.PostErrorDto;
import com.example.diploma.exception.WrongPageException;
import com.example.diploma.mappers.EntityMapper;
import com.example.diploma.model.Post;
import com.example.diploma.model.PostVote;
import com.example.diploma.model.Tag;
import com.example.diploma.model.User;
import com.example.diploma.repository.PostRepository;
import com.example.diploma.repository.TagRepository;
import com.example.diploma.repository.UserRepository;
import com.example.diploma.security.SecurityUser;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;

import javax.persistence.EntityManager;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostService {
    private final PostRepository repository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final AuthenticationManager authenticationManager;

    private EntityMapper entityMapper;

    @Autowired
    private EntityManager em;

    public PostResponse getPosts(String mode, Pageable pageable) {
        List<Post> posts;
        Page<Post> pagePost = Page.empty();
        if (mode != null) {
            pagePost = findByMode(mode, pageable);
        }

        posts = pagePost.getContent();

        List<PlainPostDto> postsList = new ArrayList<>();
        for (Post post : posts) {
            PlainPostDto postDto = entityMapper.postToPlainPostDto(post);
            postsList.add(postDto);
        }

        PostResponse response = new PostResponse();
        response.setPosts(postsList, pagePost.getTotalElements());
        return response;
    }

    private Page<Post> findByMode(String mode, Pageable pageable) {
        Page<Post> list = Page.empty();
        switch (mode) {
            case "best":
                list = repository.getBestPosts(pageable);
                break;
            case "recent":
                list = repository.findRecentPosts(pageable);
                break;
            case "popular":
                list = repository.getPopularPosts(pageable);
                break;
            case "early":
                list = repository.findEarlyPosts(pageable);
                break;
        }
        return list;
    }

    public PostWithCommentsResponse getPost(String id) {
        int postId;
        Post post;
        try {
            postId = Integer.parseInt(id);
            post = repository.findById(postId);
        } catch (Exception ex) {
            throw new WrongPageException("page not found");
        }

        List<CommentDto> comments = post.getComments().stream().map(CommentDto::new).collect(Collectors.toList());
        List<String> tags = post.getTags().stream().map(Tag::getName).collect(Collectors.toList());

        incViewCount(post);

        return new PostWithCommentsResponse(post, comments, tags);
    }

    public CalendarDto getCalendar(Integer year) {
        CalendarDto calendarDto = new CalendarDto();

        List<Post> postList = repository.getPostsForCalendar();
        calendarDto.setYears(
                postList.stream()
                        .map(p -> LocalDate.ofInstant(p.getTime(), ZoneId.systemDefault()).getYear())
                        .collect(Collectors.toSet())
        );
        Map<String, Long> list = postList.stream()
                .collect(Collectors.groupingBy(p ->
                                LocalDate.ofInstant(p.getTime(), ZoneId.of("UTC")).toString(),
                        Collectors.counting()));
        calendarDto.setPosts(list);

        return calendarDto;
    }

    private void incViewCount(Post post) {
        if (!post.getUser().equals(getCurrentUser())) {
            repository.save(post.toBuilder()
                    .viewCount(post.getViewCount() + 1).build());
        }
    }

    public PostResponse findPostsByQuery(String query, Pageable pageable) {
        Page<Post> postsPage = repository.findPostsByQuery(query, pageable);
        List<PlainPostDto> posts = postsPage.stream().map(entityMapper::postToPlainPostDto).collect(Collectors.toList());
        PostResponse response = new PostResponse();
        response.setPosts(posts, postsPage.getTotalElements());
        return response;
    }

    public PostResponse findPostsByDate(String dateTime, Pageable pageable) {
        Instant date = LocalDate.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay().toInstant(ZoneOffset.UTC);

        Page<Post> postsPage = repository.findPostsByDate(date, pageable);
        List<PlainPostDto> posts = postsPage.stream().map(entityMapper::postToPlainPostDto).collect(Collectors.toList());

        PostResponse response = new PostResponse();
        response.setPosts(posts, postsPage.getTotalElements());
        return response;
    }

    public PostResponse findPostsByTag(String tag, Pageable pageable) {
        Page<Post> postsPage = repository.findPostsByTag(tag, pageable);
        List<PlainPostDto> posts = postsPage.stream().map(entityMapper::postToPlainPostDto).collect(Collectors.toList());

        PostResponse response = new PostResponse();
        response.setPosts(posts, postsPage.getTotalElements());

        return response;
    }

    public PostResponse findPostsForModeration(ModerationStatus status, Pageable pageable) {

        Page<Post> postsPage = repository.findPostForModeration(status, pageable);
        List<PlainPostDto> posts = postsPage.stream().map(entityMapper::postToPlainPostDto).collect(Collectors.toList());

        PostResponse response = new PostResponse();
        response.setPosts(posts, postsPage.getTotalElements());

        return response;
    }

    public PostResponse findMyPosts(PostModerationStatus status, Pageable pageable) {
        PostResponse response = new PostResponse();
        String email = ((SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("username not found"));

        Page<Post> postsPage = repository.findMyPosts(
                user.getId(),
                status.getModerationStatus(),
                status.isActive(),
                pageable
        );
        List<PlainPostDto> posts = postsPage.stream().map(entityMapper::postToPlainPostDto).collect(Collectors.toList());

        response.setPosts(posts, postsPage.getTotalElements());

        return response;
    }

    @Transactional
    public ResultResponse<PostError> addNewPost(NewPostRequest request, Errors errors) {
        ResultResponse<PostError> response = new ResultResponse<>();
        //check request
        if (errors.hasErrors()) {
            response.setResult(false);
            response.setErrors(getErrors(errors));
            return response;
        }

        //create tags
        List<Tag> tags = new ArrayList<>();
        if (request.getTags() != null) {
            request.getTags().forEach(tag -> tags.add(takeTag(tag)));
        }

        //create post
        long currentTime = Instant.now().getEpochSecond();
        Post post = Post.builder()
                .isActive(request.getActive() == 1)
                .moderationStatus(ModerationStatus.NEW)
                .user(getCurrentUser())
                .tags(tags)
                .time(Instant.ofEpochSecond(Math.max(currentTime, request.getTimestamp())))
                .title(request.getTitle())
                .text(request.getText())
                .build();
        repository.save(post);

        return response;
    }

    @Transactional
    public ResultResponse<PostError> editPost(int id, NewPostRequest request, Errors errors) {
        ResultResponse<PostError> response = new ResultResponse<>();

        //check request
        if (errors.hasErrors()) {
            response.setResult(false);
            response.setErrors(getErrors(errors));
            return response;
        }

        //check tags
        List<Tag> tags = new ArrayList<>();
        if (request.getTags() != null) {
            request.getTags().forEach(tag -> tags.add(takeTag(tag)));
        }

        //update post
        long currentTime = Instant.now().getEpochSecond();
        Post post = repository.findById(id);

        //delete previous tags
        List<Tag> prevTags = post.getTags();
        tagRepository.deleteAll(prevTags);

        ModerationStatus postStatus = (getCurrentUser().getIsModerator() == 1) ?
                post.getModerationStatus() :
                ModerationStatus.NEW;

        Post editedPost = post.toBuilder()
                .isActive(request.getActive() == 1)
                .moderationStatus(postStatus)
                .user(getCurrentUser())
                .tags(tags)
                .time(Instant.ofEpochSecond(Math.max(currentTime, request.getTimestamp())))
                .title(request.getTitle())
                .text(request.getText())
                .build();

        repository.save(editedPost);

        return response;
    }

    private Tag takeTag(String name) {
        Tag tag = tagRepository.findByNameIgnoreCase(name);
        return (tag != null) ? tag : tagRepository.save(new Tag(name));
    }

    private User getCurrentUser() {
        String email = ((SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("user not found"));
    }

    private PostError getErrors(Errors errors) {
        PostError postErrors = new PostError();
        if (errors.hasFieldErrors("title")) {
            postErrors.setTitle(errors.getFieldError("title").getDefaultMessage());
        }
        if (errors.hasFieldErrors("text")) {
            postErrors.setText(errors.getFieldError("text").getDefaultMessage());
        }
        return postErrors;
    }

    public boolean moderatePost(ModerateRequest request) {
        try {
            Post post = repository.findById(request.getPostId());
            ModerationStatus decision = (request.getDecision().equals("accept")) ?
                    ModerationStatus.ACCEPTED : ModerationStatus.DECLINED;

            repository.save(post.toBuilder()
                    .moderationStatus(decision)
                    .moderator(getCurrentUser())
                    .build()
            );
        } catch (Exception exception) {
            return false;
        }
        return true;
    }


}
