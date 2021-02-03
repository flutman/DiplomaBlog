package com.example.diploma.service;

import com.example.diploma.data.request.GlobalSettingsRequest;
import com.example.diploma.data.response.GlobalSettingResponse;
import com.example.diploma.model.GlobalSetting;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;

public interface GlobalSettingService {
    ResponseEntity<GlobalSettingResponse> getAllSettings();

    void setGlobalSettings(GlobalSettingsRequest request);

    HashSet<GlobalSetting> getSiteSettings();
}
