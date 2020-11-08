package com.example.diploma.enums;

import org.springframework.core.convert.converter.Converter;

public enum SortMode {
    RECENT,
    POPULAR,
    BEST,
    EARLY;

    public static class StringToEnumConverter implements Converter<String,SortMode> {

        @Override
        public SortMode convert(String sortMode){
            return SortMode.valueOf(sortMode.toUpperCase());
        }
    }
}
