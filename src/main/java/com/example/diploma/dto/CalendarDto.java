package com.example.diploma.dto;

import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
public class CalendarDto {
    public Set<Integer> years;
    private Map<String, Long> posts;
}
