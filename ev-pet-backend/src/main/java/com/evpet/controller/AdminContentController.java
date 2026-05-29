package com.evpet.controller;

import com.evpet.service.AdminContentService;
import com.evpet.vo.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminContentController {

    private final AdminContentService adminContentService;

    @GetMapping("/reviews")
    public ApiResponse<Map<String, Object>> getReviews(
            @RequestParam(defaultValue = "pending") String status) {
        try {
            return ApiResponse.success(adminContentService.getReviews(status));
        } catch (Exception e) {
            log.error("获取审核列表失败", e);
            return ApiResponse.error("获取审核列表失败");
        }
    }

    @PostMapping("/reviews/{id}/approve")
    public ApiResponse<String> approveReview(@PathVariable Long id) {
        try {
            adminContentService.approveReview(id);
            return ApiResponse.success("已通过");
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(404, e.getMessage());
        } catch (Exception e) {
            log.error("审核操作失败", e);
            return ApiResponse.error("审核操作失败");
        }
    }

    @PostMapping("/reviews/{id}/reject")
    public ApiResponse<String> rejectReview(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            adminContentService.rejectReview(id, body.get("reason"));
            return ApiResponse.success("已驳回");
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(404, e.getMessage());
        } catch (Exception e) {
            log.error("审核操作失败", e);
            return ApiResponse.error("审核操作失败");
        }
    }
}
