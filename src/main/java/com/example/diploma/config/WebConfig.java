package com.example.diploma.config;

import com.example.diploma.enums.ModerationStatus;
import com.example.diploma.enums.PostModerationStatus;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new PostModerationStatus.StringToEnumConverter());
        registry.addConverter(new ModerationStatus.StringToEnumConverter());
    }
}
