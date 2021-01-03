package com.example.diploma.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Getter
public class AppConfig {
    private final Map<String, Integer> sessions = new HashMap<>();
    private final static int CAPTCHA_LENGTH = 7;
    @Value("${captcha.updateTime:1}")
    private int CAPTCHA_HOURS_TO_BE_UPDATED;

    public void addSession(String sessionId, Integer userId) {
        sessions.put(sessionId, userId);
    }

    public int getCaptchaLength() {
        return CAPTCHA_LENGTH;
    }

    public int getCaptchaHoursToBeUpdated() {
        return CAPTCHA_HOURS_TO_BE_UPDATED;
    }

    public Map<String, Integer> getSessions() {
        return sessions;
    }

    public Integer getUserIdBySessionId(String sessionId) {
        return sessions.remove(sessionId);
    }
}
