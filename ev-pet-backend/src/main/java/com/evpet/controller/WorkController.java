package com.evpet.controller;

import com.evpet.dto.WorkDTO;
import com.evpet.service.WorkService;
import com.evpet.vo.ApiResponse;
import com.evpet.vo.WorkVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/work")
@RequiredArgsConstructor
public class WorkController extends BaseController {

    private final WorkService workService;

    @GetMapping("/info")
    public ApiResponse<WorkVO> getWorkInfo(@RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            return ApiResponse.success(workService.getWorkInfo(userId));
        } catch (Exception e) {
            return ApiResponse.error("获取打工信息失败: " + e.getMessage());
        }
    }

    @PostMapping("/start")
    public ApiResponse<String> startWork(
            @RequestHeader("Authorization") String token,
            @RequestParam Long workId) {
        try {
            Long userId = getUserIdFromToken(token);
            return workService.startWork(userId, workId);
        } catch (Exception e) {
            return ApiResponse.error("开始打工失败: " + e.getMessage());
        }
    }

    @PostMapping("/claim")
    public ApiResponse<WorkVO.WorkStatsVO> claimReward(
            @RequestHeader("Authorization") String token,
            @RequestParam Long recordId) {
        try {
            Long userId = getUserIdFromToken(token);
            return workService.claimWorkReward(userId, recordId);
        } catch (Exception e) {
            return ApiResponse.error("领取奖励失败: " + e.getMessage());
        }
    }

}
