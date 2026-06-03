package com.example.aijobhelper.controller;

import com.example.aijobhelper.model.vo.ApiResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 健康检查接口
 */
@RestController
public class HealthController {

    @GetMapping("/api/health")
    public ApiResult<Map<String, Object>> health() {
        return ApiResult.ok(Map.of(
                "status", "UP",
                "service", "ai-job-helper",
                "time", LocalDateTime.now().toString()
        ));
    }
}
