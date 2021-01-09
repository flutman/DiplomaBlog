package com.example.diploma.controller;

import com.example.diploma.data.response.TagResponse;
import com.example.diploma.dto.CalendarDto;
import com.example.diploma.data.response.StatisticResponse;
import com.example.diploma.service.GlobalSettingService;
import com.example.diploma.service.PostService;
import com.example.diploma.service.TagService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor
@RequestMapping("/api")
public class ApiGeneralController {
    private final TagService tagService;
    private final PostService postService;
    private final GlobalSettingService globalSettingService;

    @GetMapping("/tag")
    public ResponseEntity<TagResponse> showTags(@RequestParam(required = false) String query){
        return new ResponseEntity<>(tagService.getAllTags(), HttpStatus.OK);
    }

    @GetMapping("/calendar")
    public ResponseEntity<CalendarDto> showCalendar(@RequestParam(required = false) Integer year) {
        return new ResponseEntity<CalendarDto>(postService.getCalendar(year), HttpStatus.OK);
    }

    @GetMapping("/statistics/all")
    public ResponseEntity<StatisticResponse> showStatistics() {
        return globalSettingService.getStatistics();
    }


}
