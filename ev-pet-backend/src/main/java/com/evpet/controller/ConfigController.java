package com.evpet.controller;

import com.evpet.service.SystemConfigService;
import com.evpet.vo.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/admin/config")
@RequiredArgsConstructor
@Slf4j
public class ConfigController {

    private final SystemConfigService systemConfigService;

    @GetMapping
    public ApiResponse<Map<String, String>> getAll() {
        try {
            return ApiResponse.success(systemConfigService.getAllAsMap());
        } catch (Exception e) {
            return ApiResponse.error("获取配置失败: " + e.getMessage());
        }
    }

    @PostMapping
    public ApiResponse<String> save(@RequestBody Map<String, String> configMap) {
        try {
            systemConfigService.batchSave(configMap);
            return ApiResponse.success("配置已保存");
        } catch (Exception e) {
            return ApiResponse.error("保存配置失败: " + e.getMessage());
        }
    }

    @PostMapping("/test-ai")
    public ApiResponse<String> testAI() {
        try {
            String apiUrl = systemConfigService.getByKey("ai_api_url");
            String apiKey = systemConfigService.getByKey("ai_api_key");
            String model = systemConfigService.getByKeyOrDefault("ai_model", "MiniMax-Text-01");

            if (apiUrl == null || apiUrl.isEmpty()) {
                return ApiResponse.error("AI API地址未配置");
            }
            if (apiKey == null || apiKey.isEmpty()) {
                return ApiResponse.error("AI API密钥未配置");
            }

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build();

            String requestBody = String.format(
                    "{\"model\":\"%s\",\"messages\":[{\"role\":\"user\",\"content\":\"你好\"}],\"stream\":false}",
                    model
            );

            Request request = new Request.Builder()
                    .url(apiUrl)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(okhttp3.RequestBody.create(requestBody, MediaType.parse("application/json; charset=utf-8")))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    return ApiResponse.success("AI服务连接正常");
                } else {
                    String body = response.body() != null ? response.body().string() : "";
                    return ApiResponse.error("AI服务返回错误: HTTP " + response.code() + " " + body);
                }
            }
        } catch (Exception e) {
            log.error("AI连接测试失败", e);
            return ApiResponse.error("AI服务连接失败: " + e.getMessage());
        }
    }
}
