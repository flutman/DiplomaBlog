package com.example.diploma.service;

import com.example.diploma.data.PostResponse;
import com.example.diploma.data.PostWithCommentsResponse;
import com.example.diploma.dto.CalendarDto;
import com.example.diploma.dto.CommentDto;
import com.example.diploma.dto.PlainPostDto;
import com.example.diploma.mappers.EntityMapper;
import com.example.diploma.model.Post;
import com.example.diploma.model.Tag;
import com.example.diploma.repository.PostRepository;
import javassist.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.lang.reflect.Array;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostService {
    private final PostRepository repository;
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
//                list = repository.getPopularPosts(pageable);
//                list = repository.getPopularPosts(pageable).map(Map.Entry::getKey);
                Page<Post> mapEntPost = repository.getPopularPosts(pageable);
//                mapEntPost.map(item -> {
//                   Post post = new Post();
//                    for (Map.Entry<Post, Long> entry : item.entrySet()) {
//                       post = entry.getKey();
//                   }
//                    return post;
//                });
                break;
            case "early":
                list = repository.findEarlyPosts(pageable);
                break;
        }
        return list;
    }

    public PostWithCommentsResponse getPostById(int id) throws NotFoundException {
        Post post = repository.findById(id).orElseThrow(() -> new NotFoundException("not found"));
        List<CommentDto> comments = post.getComments().stream().map(CommentDto::new).collect(Collectors.toList());
        List<String> tags = post.getTags().stream().map(Tag::getName).collect(Collectors.toList());

        incViewCount(post);

        PostWithCommentsResponse response = new PostWithCommentsResponse(post, comments, tags);

        return response;
    }

    public CalendarDto getCalendar(Integer year) {
        CalendarDto calendarDto = new CalendarDto();

        List<Post> postList = repository.findAllByOrderByTime();
        calendarDto.setYears(
                postList.stream()
                        .map(p -> LocalDate.ofInstant(p.getTime(), ZoneId.systemDefault()).getYear())
                        .collect(Collectors.toSet())
        );
        Map<String, Long> list = postList.stream()
                .collect(Collectors.groupingBy(p ->
                        LocalDate.ofInstant(p.getTime(), ZoneId.systemDefault()).toString(),
                        Collectors.counting()));
        calendarDto.setPosts(list);

        return calendarDto;
    }

    private void incViewCount(Post post) {
        //TODO добавить условия по типу пользователя
        post.setViewCount(post.getViewCount() + 1);
        repository.save(post);
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
}