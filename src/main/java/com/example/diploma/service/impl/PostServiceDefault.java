package com.example.diploma.service.impl;

import com.example.diploma.data.request.ModerateRequest;
import com.example.diploma.data.request.NewPostRequest;
import com.example.diploma.data.response.PostResponse;
import com.example.diploma.data.response.PostWithCommentsResponse;
import com.example.diploma.data.response.base.ResultResponse;
import com.example.diploma.data.response.type.NewPostResponse;
import com.example.diploma.dto.CalendarDto;
import com.example.diploma.dto.CommentDto;
import com.example.diploma.dto.PlainPostDto;
import com.example.diploma.enums.GlobalSettings;
import com.example.diploma.enums.ModerationStatus;
import com.example.diploma.enums.PostModerationStatus;
import com.example.diploma.exception.WrongPageException;
import com.example.diploma.mappers.EntityMapper;
import com.example.diploma.model.GlobalSetting;
import com.example.diploma.model.Post;
import com.example.diploma.model.Tag;
import com.example.diploma.model.User;
import com.example.diploma.repository.PostRepository;
import com.example.diploma.repository.UserRepository;
import com.example.diploma.security.SecurityUser;
import com.example.diploma.service.GlobalSettingService;
import com.example.diploma.service.PostService;
import com.example.diploma.service.TagService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostServiceDefault implements PostService {
    private final PostRepository repository;
    private final UserRepository userRepository;
    private final TagService tagService;
    private final GlobalSettingService globalSettingService;

    private EntityMapper entityMapper;

    @Override
    public PostResponse getPosts(String mode, Pageable pageable) {
        Page<Post> pagePost = findByMode(mode, pageable);
        List<Post> posts = pagePost.getContent();

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

    @Override
    public PostWithCommentsResponse getPost(Integer id) {
        Post post = repository.findById(id).orElseThrow(
                () -> new WrongPageException("page not found")
        );

        List<CommentDto> comments = getPostComment(post);
        List<String> tags = getPostTags(post);

        increaseViewCount(post);

        return new PostWithCommentsResponse(post, comments, tags);
    }

    private List<String> getPostTags(Post post) {
        return post.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
    }

    private List<CommentDto> getPostComment(Post post) {
        return post.getComments()
                .stream()
                .map(CommentDto::new)
                .collect(Collectors.toList());
    }

    @Override
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

    private void increaseViewCount(Post post) {
        User currUser = checkCurrentUser();
        if (currUser.getId() != 0) {
            if (post.getUser().equals(currUser)) {
                return;
            }
        }
        repository.save(post.toBuilder()
                .viewCount(post.getViewCount() + 1).build());
    }

    @Override
    public PostResponse findPostsByQuery(String query, Pageable pageable) {
        Page<Post> postsPage = repository.findPostsByQuery(query, pageable);
        List<PlainPostDto> posts = postsPage.stream().map(entityMapper::postToPlainPostDto).collect(Collectors.toList());
        PostResponse response = new PostResponse();
        response.setPosts(posts, postsPage.getTotalElements());
        return response;
    }

    @Override
    public PostResponse findPostsByDate(String dateTime, Pageable pageable) {
        Instant date = LocalDate.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay().toInstant(ZoneOffset.UTC);

        Page<Post> postsPage = repository.findPostsByDate(date, pageable);
        List<PlainPostDto> posts = postsPage.stream().map(entityMapper::postToPlainPostDto).collect(Collectors.toList());

        PostResponse response = new PostResponse();
        response.setPosts(posts, postsPage.getTotalElements());
        return response;
    }

    @Override
    public PostResponse findPostsByTag(String tag, Pageable pageable) {
        Page<Post> postsPage = repository.findPostsByTag(tag, pageable);
        List<PlainPostDto> posts = postsPage.stream().map(entityMapper::postToPlainPostDto).collect(Collectors.toList());

        PostResponse response = new PostResponse();
        response.setPosts(posts, postsPage.getTotalElements());

        return response;
    }

    @Override
    public PostResponse findPostsForModeration(ModerationStatus status, Pageable pageable) {

        Page<Post> postsPage = repository.findPostForModeration(status, pageable);
        List<PlainPostDto> posts = postsPage.stream().map(entityMapper::postToPlainPostDto).collect(Collectors.toList());

        PostResponse response = new PostResponse();
        response.setPosts(posts, postsPage.getTotalElements());

        return response;
    }

    @Override
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

    @Override
    @Transactional
    public ResultResponse<NewPostResponse> addNewPost(NewPostRequest request, Errors errors) {
        ResultResponse<NewPostResponse> response = new ResultResponse<>();
        //check request
        if (errors.hasErrors()) {
            response.setResult(false);
            response.setErrors(getErrors(errors));
            return response;
        }

        List<Tag> tags = newPostTags(request);

        //create post
        long currentTime = Instant.now().getEpochSecond();
        HashSet<GlobalSetting> siteSettings = globalSettingService.getSiteSettings();
        ModerationStatus moderationStatus = ModerationStatus.NEW;
        for (GlobalSetting setting : siteSettings) {
            if (setting.getCode().equals(GlobalSettings.Code.POST_PREMODERATION)) {
                if (!setting.getValue().getValue()) {
                    moderationStatus = ModerationStatus.ACCEPTED;
                }
            }
        }

        Post post = Post.builder()
                .isActive(request.getActive() == 1)
                .moderationStatus(moderationStatus)
                .user(checkCurrentUser())
                .tags(tags)
                .time(Instant.ofEpochSecond(Math.max(currentTime, request.getTimestamp())))
                .title(request.getTitle())
                .text(request.getText())
                .build();
        repository.save(post);

        return response;
    }

    private List<Tag> newPostTags(NewPostRequest request) {
        List<Tag> tags = new ArrayList<>();
        if (request.getTags() != null) {
            request.getTags().forEach(tag -> tags.add(takeTag(tag)));
        }
        return tags;
    }

    @Override
    @Transactional
    public ResultResponse<NewPostResponse> editPost(Integer id, NewPostRequest request, Errors errors) {
        ResultResponse<NewPostResponse> response = new ResultResponse<>();

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
        Post post = repository.findById(id).orElseThrow(
                () -> new WrongPageException("page not found")
        );

        //delete previous tags
        List<Tag> prevTags = post.getTags();
        tagService.deletePrevTags(prevTags);

        ModerationStatus postStatus = (checkCurrentUser().getIsModerator() == 1) ?
                post.getModerationStatus() :
                ModerationStatus.NEW;

        Post editedPost = post.toBuilder()
                .isActive(request.getActive() == 1)
                .moderationStatus(postStatus)
                .user(checkCurrentUser())
                .tags(tags)
                .time(Instant.ofEpochSecond(Math.max(currentTime, request.getTimestamp())))
                .title(request.getTitle())
                .text(request.getText())
                .build();

        repository.save(editedPost);

        return response;
    }

    private Tag takeTag(String name) {
        Tag tag = tagService.findTagByName(name);
        return (tag != null) ? tag : tagService.saveNewTag(name);
    }

    public User checkCurrentUser() {
        Object currUser = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(currUser instanceof SecurityUser)) {
            return new User();
        }
        String email = ((SecurityUser) currUser).getUsername();
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("user not found"));
    }

    private NewPostResponse getErrors(Errors errors) {
        NewPostResponse postErrors = new NewPostResponse();
        if (errors.hasFieldErrors("title")) {
            postErrors.setTitle(errors.getFieldError("title").getDefaultMessage());
        }
        if (errors.hasFieldErrors("text")) {
            postErrors.setText(errors.getFieldError("text").getDefaultMessage());
        }
        return postErrors;
    }

    @Override
    public boolean moderatePost(ModerateRequest request) {
        try {
            Post post = repository.findById(request.getPostId()).orElseThrow(
                    () -> new WrongPageException("page not found")
            );
            ModerationStatus decision = (request.getDecision().equals("accept")) ?
                    ModerationStatus.ACCEPTED : ModerationStatus.DECLINED;

            repository.save(post.toBuilder()
                    .moderationStatus(decision)
                    .moderator(checkCurrentUser())
                    .build()
            );
        } catch (Exception exception) {
            return false;
        }
        return true;
    }

    @Override
    public Post findPostById(Integer id) {
        return repository.findById(id).orElseThrow(
                () -> new WrongPageException("post not found")
        );
    }
}
