package com.example.diploma.service;

import com.example.diploma.data.response.StatisticResponse;
import com.example.diploma.enums.StatisticsType;
import org.springframework.http.ResponseEntity;

public interface StatisticService {
    ResponseEntity<StatisticResponse> getStatistics(StatisticsType type);
}
