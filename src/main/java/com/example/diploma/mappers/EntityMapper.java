package com.example.diploma.mappers;

import com.example.diploma.dto.PlainPostDto;
import com.example.diploma.dto.TagDto;
import com.example.diploma.dto.UserDto;
import com.example.diploma.model.Post;
import com.example.diploma.model.Tag;
import com.example.diploma.model.User;
import org.springframework.stereotype.Service;

@Service
public class EntityMapper {
    public EntityMapper() {
    }

    public PlainPostDto postToPlainPostDto(Post post) {
        PlainPostDto plainPostDto = new PlainPostDto();

        plainPostDto.setCommentCount(post.getComments().size());
        plainPostDto.setId(post.getPid());
        plainPostDto.setTitle(post.getTitle());
        plainPostDto.setViewCount(post.getViewCount());
        plainPostDto.setTime(post.getPtime().getEpochSecond());
        plainPostDto.setUser(userToUserDto(post.getUser()));
        String announce = post.getText().length() > 150 ? post.getText().substring(0, 150) + "..." : post.getText();
        plainPostDto.setAnnounce(announce);
        plainPostDto.setDislikeCount(post.getPostVotes().stream()
                .filter(item -> item.getValue() < 0)
                .count());
        plainPostDto.setLikeCount(post.getPostVotes().stream()
                .filter(item -> item.getValue() > 0)
                .count());

        return plainPostDto;
    }

    public UserDto userToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        return userDto;
    }

    public TagDto tagToTagDto(Tag tag, long allPostList) {
        TagDto tagDto = new TagDto();
        tagDto.setName(tag.getName());
        Double weight = (( (double) tag.getPosts().size() / allPostList) * allPostList / 10 );

        tagDto.setWeight(weight);
        return tagDto;
    }

}
