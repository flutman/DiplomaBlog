package com.example.diploma.enums;

import lombok.Getter;
import org.springframework.core.convert.converter.Converter;

@Getter
public enum PostModerationStatus {
    INACTIVE(false, ModerationStatus.NEW),
    PENDING(true, ModerationStatus.NEW),
    DECLINED(true, ModerationStatus.DECLINED),
    PUBLISHED(true, ModerationStatus.ACCEPTED);

    private final boolean active;
    private final ModerationStatus moderationStatus;

    PostModerationStatus(boolean active, ModerationStatus moderationStatus) {
        this.active = active;
        this.moderationStatus = moderationStatus;
    }

    public static class StringToEnumConverter implements Converter<String, PostModerationStatus> {
        @Override
        public PostModerationStatus convert(String s) {
            return PostModerationStatus.valueOf(s.toUpperCase());
        }
    }

}
