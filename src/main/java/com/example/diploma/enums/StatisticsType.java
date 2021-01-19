package com.example.diploma.enums;

import org.springframework.core.convert.converter.Converter;

public enum StatisticsType {
    ALL("all"),
    MY("my");

    private final String name;

    StatisticsType(String name){
        this.name = name;
    }

    public static class StringToEnumConverter implements Converter<String, StatisticsType> {

        @Override
        public StatisticsType convert(String s) {
            return StatisticsType.valueOf(s.toUpperCase());
        }
    }

}
