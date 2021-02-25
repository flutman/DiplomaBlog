package com.example.diploma.mappers;

import com.example.diploma.dto.PlainPostDto;
import com.example.diploma.dto.TagDto;
import com.example.diploma.dto.UserDto;
import com.example.diploma.model.Post;
import com.example.diploma.model.Tag;
import com.example.diploma.model.User;
import com.example.diploma.repository.PostRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public class EntityMapper {

    public PlainPostDto postToPlainPostDto(Post post) {
        PlainPostDto plainPostDto = new PlainPostDto();

        plainPostDto.setCommentCount(post.getComments().size());
        plainPostDto.setId(post.getId());
        plainPostDto.setTitle(post.getTitle());
        plainPostDto.setViewCount(post.getViewCount());
        plainPostDto.setTimestamp(post.getTime().getEpochSecond());
        plainPostDto.setUser(userToUserDto(post.getUser()));
        String announce = post.getText().replaceAll("<.*?>","");
        announce = announce.length() > 150 ? announce.substring(0, 150) + "..." : announce;
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

    public TagDto tagToTagDto(String tagName, long postWithTagCount, long postsCount) {
        TagDto tagDto = new TagDto();
        tagDto.setName(tagName);
        double weight = (( (double) postWithTagCount / postsCount) * postsCount / 10 );

        tagDto.setWeight(weight);
        return tagDto;
    }

}
