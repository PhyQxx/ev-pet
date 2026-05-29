package com.evpet.controller;

import com.evpet.service.AdminAnnouncementService;
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
public class AdminAnnouncementController {

    private final AdminAnnouncementService adminAnnouncementService;

    @GetMapping("/announcements")
    public ApiResponse<List<Map<String, Object>>> getAnnouncements(
            @RequestParam(required = false) String type) {
        try {
            return ApiResponse.success(adminAnnouncementService.getAnnouncements(type));
        } catch (Exception e) {
            log.error("获取公告列表失败", e);
            return ApiResponse.error("获取公告列表失败");
        }
    }

    @PostMapping("/announcements")
    public ApiResponse<String> createAnnouncement(@RequestBody Map<String, Object> data) {
        try {
            adminAnnouncementService.createAnnouncement(data);
            return ApiResponse.success("创建成功");
        } catch (Exception e) {
            log.error("创建公告失败", e);
            return ApiResponse.error("创建公告失败");
        }
    }

    @PutMapping("/announcements/{id}")
    public ApiResponse<String> updateAnnouncement(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        try {
            adminAnnouncementService.updateAnnouncement(id, data);
            return ApiResponse.success("更新成功");
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(404, e.getMessage());
        } catch (Exception e) {
            log.error("更新公告失败", e);
            return ApiResponse.error("更新公告失败");
        }
    }

    @DeleteMapping("/announcements/{id}")
    public ApiResponse<String> deleteAnnouncement(@PathVariable Long id) {
        try {
            adminAnnouncementService.deleteAnnouncement(id);
            return ApiResponse.success("删除成功");
        } catch (Exception e) {
            log.error("删除公告失败", e);
            return ApiResponse.error("删除公告失败");
        }
    }

    @PostMapping("/announcements/{id}/publish")
    public ApiResponse<String> publishAnnouncement(@PathVariable Long id) {
        try {
            adminAnnouncementService.publishAnnouncement(id);
            return ApiResponse.success("发布成功");
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(404, e.getMessage());
        } catch (Exception e) {
            log.error("发布公告失败", e);
            return ApiResponse.error("发布公告失败");
        }
    }

    @PostMapping("/announcements/{id}/withdraw")
    public ApiResponse<String> withdrawAnnouncement(@PathVariable Long id) {
        try {
            adminAnnouncementService.withdrawAnnouncement(id);
            return ApiResponse.success("撤回成功");
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(404, e.getMessage());
        } catch (Exception e) {
            log.error("撤回公告失败", e);
            return ApiResponse.error("撤回公告失败");
        }
    }
}
