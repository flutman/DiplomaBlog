package com.example.diploma.dto;

import lombok.Data;

@Data
public class TagDto implements Comparable<TagDto> {
    private String name;
    private Double weight;

    @Override
    public int compareTo(TagDto o) {
        int result = 0;

        return Double.compare(this.weight, o.getWeight());
    }
}
