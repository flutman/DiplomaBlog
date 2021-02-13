package com.example.diploma.service.impl;

import com.example.diploma.data.response.TagResponse;
import com.example.diploma.dto.TagDto;
import com.example.diploma.mappers.EntityMapper;
import com.example.diploma.model.Tag;
import com.example.diploma.repository.PostRepository;
import com.example.diploma.repository.TagRepository;
import com.example.diploma.service.TagService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class TagServiceDefault implements TagService {
    private final TagRepository repository;
    private final PostRepository postRepository;

    public TagServiceDefault(TagRepository repository, PostRepository postRepository) {
        this.repository = repository;
        this.postRepository = postRepository;
    }

    public TagResponse getTags() {
        List<Tag> tags = new ArrayList<>(repository.findTagOfPublishedPosts());
        EntityMapper entityMapper = new EntityMapper();

        List<TagDto> tagsList = new ArrayList<>();
        long postSize = postRepository.getCountPosts();
        for (Tag tag : tags) {
            TagDto tagDto = entityMapper.tagToTagDto(tag, postSize);
            tagsList.add(tagDto);
        }

        TagResponse response = new TagResponse();

        response.setTags(normalizeTags(tagsList));

        return response;
    }

    private List<TagDto> normalizeTags(List<TagDto> tags) {
        tags.sort(Comparator.reverseOrder());

        double k = 1 / tags.get(0).getWeight();
        tags.stream().skip(1)
                .forEach(tagDto -> {
                    double newWeight = tagDto.getWeight() * k;
                    tagDto.setWeight(newWeight);
                });
        tags.get(0).setWeight(1.0);

        return tags;
    }

}
