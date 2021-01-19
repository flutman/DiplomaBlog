package com.example.diploma.config;

import com.example.diploma.enums.ModerationStatus;
import com.example.diploma.enums.PostModerationStatus;
import com.example.diploma.enums.StatisticsType;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Path;

import static org.assertj.core.util.Strings.concat;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new PostModerationStatus.StringToEnumConverter());
        registry.addConverter(new ModerationStatus.StringToEnumConverter());
        registry.addConverter(new StatisticsType.StringToEnumConverter());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/upload/**","/img/upload/**","/static/img/upload/**")
                .addResourceLocations("file:D:/javaProg/diploma/Diploma/upload/","file:D:/javaProg/diploma/Diploma/");
    }
}
