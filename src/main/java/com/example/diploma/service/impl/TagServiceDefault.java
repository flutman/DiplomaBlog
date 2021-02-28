package com.example.diploma.service.impl;

import com.example.diploma.data.response.TagResponse;
import com.example.diploma.dto.TagDto;
import com.example.diploma.mappers.EntityMapper;
import com.example.diploma.model.Tag;
import com.example.diploma.repository.PostRepository;
import com.example.diploma.repository.TagRepository;
import com.example.diploma.service.TagService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class TagServiceDefault implements TagService {
    private final TagRepository repository;
    private final PostRepository postRepository;
    private final EntityMapper entityMapper;

    @Cacheable("tags")
    public TagResponse getTags() {
        List<Tag> tags = new ArrayList<>(repository.findTagOfPublishedPosts());

        List<TagDto> tagsList = new ArrayList<>();
        long postSize = postRepository.getCountPosts();
        for (Tag tag : tags) {
            log.info("!!!!!! TAG stat request!!!!!!");
            long postCountWithTag = postRepository.findPostCountByTag(tag.getName());
            String tagName = tag.getName();
            TagDto tagDto = entityMapper.tagToTagDto(tagName, postCountWithTag, postSize);
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

    @Override
    @CachePut(value = "tags", key = "#name")
    public Tag saveNewTag(String name) {
        return repository.save(new Tag(name));
    }

    @Override
    public void deletePrevTags(List<Tag> tagList) {
        repository.deleteAll(tagList);
    }

    @Override
    public Tag findTagByName(String name) {
        return repository.findByNameIgnoreCase(name);
    }
}
