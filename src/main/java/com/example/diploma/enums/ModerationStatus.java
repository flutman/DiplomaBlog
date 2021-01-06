package com.example.diploma.enums;

import org.springframework.core.convert.converter.Converter;

public enum ModerationStatus {
    NEW,
    ACCEPTED,
    DECLINED;

    public static class StringToEnumConverter implements Converter<String, ModerationStatus> {

        @Override
        public ModerationStatus convert(String source) {
            return ModerationStatus.valueOf(source.toUpperCase());
        }
    }
}
