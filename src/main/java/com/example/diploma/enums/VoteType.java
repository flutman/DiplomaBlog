package com.example.diploma.enums;

import org.springframework.core.convert.converter.Converter;

public enum VoteType {
    LIKE,
    DISLIKE;

    public static class StringToEnumConverter implements Converter<String, VoteType> {
        @Override
        public VoteType convert(String s) {
            return VoteType.valueOf(s.toUpperCase());
        }
    }
}
