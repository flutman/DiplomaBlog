package com.example.diploma.data.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserResponse {
    private final long id;
    private final String name;
    private final String photo;
    private final String email;
    private final boolean moderation;
    private final int moderationCount;
    private final boolean settings;

}
