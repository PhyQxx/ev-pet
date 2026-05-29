package com.evpet.controller;

import com.evpet.service.AdminAccountService;
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
public class AdminAccountController {

    private final AdminAccountService adminAccountService;

    @GetMapping("/admins")
    public ApiResponse<List<Map<String, Object>>> getAdmins() {
        return ApiResponse.success(adminAccountService.getAdmins());
    }

    @PostMapping("/admins")
    public ApiResponse<String> createAdmin(@RequestBody Map<String, Object> data) {
        adminAccountService.createAdmin(data);
        return ApiResponse.success("创建成功");
    }

    @PutMapping("/admins/{id}")
    public ApiResponse<String> updateAdmin(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        adminAccountService.updateAdmin(id, data);
        return ApiResponse.success("更新成功");
    }

    @DeleteMapping("/admins/{id}")
    public ApiResponse<String> deleteAdmin(@PathVariable Long id) {
        adminAccountService.deleteAdmin(id);
        return ApiResponse.success("删除成功");
    }

    @GetMapping("/roles")
    public ApiResponse<List<Map<String, Object>>> getRoles() {
        return ApiResponse.success(adminAccountService.getRoles());
    }

    @PutMapping("/roles/{id}")
    public ApiResponse<String> updateRole(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        adminAccountService.updateRole(id, data);
        return ApiResponse.success("更新成功");
    }
}
