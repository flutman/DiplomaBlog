package com.example.diploma.config;

import com.example.diploma.enums.ModerationStatus;
import com.example.diploma.enums.PostModerationStatus;
import com.example.diploma.enums.StatisticsType;
import com.example.diploma.enums.VoteType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${app.upload-path}")
    private String uploadPath;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new PostModerationStatus.StringToEnumConverter());
        registry.addConverter(new ModerationStatus.StringToEnumConverter());
        registry.addConverter(new StatisticsType.StringToEnumConverter());
        registry.addConverter(new VoteType.StringToEnumConverter());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**", "/img/upload/**", "/static/img/upload/**")
                .addResourceLocations("file:" + uploadPath);
    }
}
