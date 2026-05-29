package com.evpet.controller;

import com.evpet.model.User;
import com.evpet.service.AdminUserService;
import com.evpet.vo.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping("/users")
    public ApiResponse<Map<String, Object>> getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type) {
        try {
            return ApiResponse.success(adminUserService.getUsers(page, pageSize, keyword, status, type));
        } catch (Exception e) {
            log.error("获取用户列表失败", e);
            return ApiResponse.error("获取用户列表失败");
        }
    }

    @GetMapping("/users/{id}")
    public ApiResponse<User> getUser(@PathVariable Long id) {
        User user = adminUserService.getUserById(id);
        if (user == null) return ApiResponse.error(404, "用户不存在");
        return ApiResponse.success(user);
    }

    @PutMapping("/users/{id}")
    public ApiResponse<String> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        try {
            adminUserService.updateUser(id, data);
            return ApiResponse.success("更新成功");
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(404, e.getMessage());
        } catch (Exception e) {
            log.error("更新用户失败", e);
            return ApiResponse.error("更新用户失败");
        }
    }

    @PostMapping("/users/{id}/ban")
    public ApiResponse<String> banUser(@PathVariable Long id) {
        try {
            adminUserService.banUser(id);
            return ApiResponse.success("禁用成功");
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(404, e.getMessage());
        } catch (Exception e) {
            log.error("禁用用户失败", e);
            return ApiResponse.error("禁用用户失败");
        }
    }

    @PostMapping("/users/{id}/unban")
    public ApiResponse<String> unbanUser(@PathVariable Long id) {
        try {
            adminUserService.unbanUser(id);
            return ApiResponse.success("启用成功");
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(404, e.getMessage());
        } catch (Exception e) {
            log.error("启用用户失败", e);
            return ApiResponse.error("启用用户失败");
        }
    }
}
