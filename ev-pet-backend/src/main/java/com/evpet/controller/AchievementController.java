package com.evpet.controller;

import com.evpet.service.AchievementService;
import com.evpet.utils.JwtUtil;
import com.evpet.vo.ApiResponse;
import com.evpet.vo.AchievementVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/achievement")
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService achievementService;
    private final JwtUtil jwtUtil;

    @GetMapping("/list")
    public ApiResponse<AchievementVO> getAchievements(@RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            return ApiResponse.success(achievementService.getAchievements(userId));
        } catch (Exception e) {
            return ApiResponse.error("获取成就列表失败: " + e.getMessage());
        }
    }

    @PostMapping("/claim")
    public ApiResponse<String> claimReward(
            @RequestHeader("Authorization") String token,
            @RequestParam Long achievementId) {
        try {
            Long userId = getUserIdFromToken(token);
            return achievementService.claimReward(userId, achievementId);
        } catch (Exception e) {
            return ApiResponse.error("领取奖励失败: " + e.getMessage());
        }
    }

    private Long getUserIdFromToken(String token) {
        String actualToken = token.replace("Bearer ", "");
        return jwtUtil.getUserIdFromToken(actualToken);
    }
}
