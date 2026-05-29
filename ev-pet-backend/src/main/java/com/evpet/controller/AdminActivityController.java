package com.evpet.controller;

import com.evpet.service.AdminActivityService;
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
public class AdminActivityController {

    private final AdminActivityService adminActivityService;

    @GetMapping("/activities")
    public ApiResponse<List<Map<String, Object>>> getActivities() {
        try {
            return ApiResponse.success(adminActivityService.getActivities());
        } catch (Exception e) {
            log.error("获取活动列表失败", e);
            return ApiResponse.error("获取活动列表失败");
        }
    }

    @PostMapping("/activities")
    public ApiResponse<String> createActivity(@RequestBody Map<String, Object> data) {
        try {
            adminActivityService.createActivity(data);
            return ApiResponse.success("创建成功");
        } catch (Exception e) {
            log.error("创建活动失败", e);
            return ApiResponse.error("创建活动失败");
        }
    }

    @PutMapping("/activities/{id}")
    public ApiResponse<String> updateActivity(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        try {
            adminActivityService.updateActivity(id, data);
            return ApiResponse.success("更新成功");
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(404, e.getMessage());
        } catch (Exception e) {
            log.error("更新活动失败", e);
            return ApiResponse.error("更新活动失败");
        }
    }

    @DeleteMapping("/activities/{id}")
    public ApiResponse<String> deleteActivity(@PathVariable Long id) {
        try {
            adminActivityService.deleteActivity(id);
            return ApiResponse.success("删除成功");
        } catch (Exception e) {
            log.error("删除活动失败", e);
            return ApiResponse.error("删除活动失败");
        }
    }

    @PostMapping("/activities/{id}/end")
    public ApiResponse<String> endActivity(@PathVariable Long id) {
        try {
            adminActivityService.endActivity(id);
            return ApiResponse.success("活动已结束");
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(404, e.getMessage());
        } catch (Exception e) {
            log.error("结束活动失败", e);
            return ApiResponse.error("结束活动失败");
        }
    }
}
