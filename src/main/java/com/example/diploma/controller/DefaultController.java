package com.example.diploma.controller;

import com.example.diploma.data.GlobalSettingResponse;
import com.example.diploma.data.InitResponse;
import com.example.diploma.dto.SettingsValues;
import com.example.diploma.service.GlobalSettingService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@AllArgsConstructor
public class DefaultController {

    private final GlobalSettingService globalSettingService;

    @RequestMapping(method = {RequestMethod.OPTIONS, RequestMethod.GET}, value = "/**/{path:[^\\.]*}")
    public String redirectToIndex(){
        return "forward:/";
    }

    @RequestMapping("/")
    public String index(){
        return "index";
    }

    @GetMapping("/api/init")
    public ResponseEntity<InitResponse> init(){
        return ResponseEntity.ok(new InitResponse());
    }

    @GetMapping("/api/settings")
    public ResponseEntity<GlobalSettingResponse> getGlobalSettings() {
        GlobalSettingResponse response = new GlobalSettingResponse();
        SettingsValues settings = globalSettingService.getAllSettings();
        response.setMULTIUSER_MODE(settings.getMultiuserMode());
        response.setPOST_PREMODERATION(settings.getPostPremoderation());
        response.setSTATISTICS_IS_PUBLIC(settings.getStatisticsIsPublic());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/error")
    public String handleError(){
        return "forward:";
    }

}
