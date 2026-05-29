package com.evpet.controller;

import com.evpet.service.AdminAccountService;
import com.evpet.service.AdminStatsService;
import com.evpet.vo.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminAuthController {

    private final AdminAccountService adminAccountService;
    private final AdminStatsService adminStatsService;

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        Map<String, Object> result = adminAccountService.login(username, password);
        if (result == null) {
            return ApiResponse.error(401, "用户名或密码错误");
        }
        return ApiResponse.success(result);
    }

    @GetMapping("/stats/overview")
    public ApiResponse<Map<String, Object>> getOverview() {
        try {
            return ApiResponse.success(adminStatsService.getOverview());
        } catch (Exception e) {
            log.error("获取统计概览失败", e);
            return ApiResponse.error("获取统计概览失败");
        }
    }

    @GetMapping("/stats/dau")
    public ApiResponse<List<Map<String, Object>>> getDauTrend(
            @RequestParam(defaultValue = "7") int days) {
        return ApiResponse.success(adminStatsService.getDauTrend(days));
    }

    @GetMapping("/stats/revenue")
    public ApiResponse<List<Map<String, Object>>> getRevenueTrend(
            @RequestParam(defaultValue = "7") int days) {
        return ApiResponse.success(adminStatsService.getRevenueTrend(days));
    }
}
