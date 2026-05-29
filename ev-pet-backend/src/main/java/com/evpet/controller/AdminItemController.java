package com.evpet.controller;

import com.evpet.model.Item;
import com.evpet.service.AdminItemService;
import com.evpet.vo.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminItemController {

    private final AdminItemService adminItemService;

    @GetMapping("/items")
    public ApiResponse<Map<String, Object>> getItems(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int pageSize,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer status) {
        try {
            return ApiResponse.success(adminItemService.getItems(page, pageSize, category, status));
        } catch (Exception e) {
            log.error("获取道具列表失败", e);
            return ApiResponse.error("获取道具列表失败");
        }
    }

    @PostMapping("/items")
    public ApiResponse<String> createItem(@RequestBody Item item) {
        try {
            adminItemService.createItem(item);
            return ApiResponse.success("创建成功");
        } catch (Exception e) {
            log.error("创建道具失败", e);
            return ApiResponse.error("创建道具失败");
        }
    }

    @PutMapping("/items/{id}")
    public ApiResponse<String> updateItem(@PathVariable Long id, @RequestBody Item data) {
        try {
            adminItemService.updateItem(id, data);
            return ApiResponse.success("更新成功");
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(404, e.getMessage());
        } catch (Exception e) {
            log.error("更新道具失败", e);
            return ApiResponse.error("更新道具失败");
        }
    }

    @DeleteMapping("/items/{id}")
    public ApiResponse<String> deleteItem(@PathVariable Long id) {
        try {
            adminItemService.deleteItem(id);
            return ApiResponse.success("删除成功");
        } catch (Exception e) {
            log.error("删除道具失败", e);
            return ApiResponse.error("删除道具失败");
        }
    }

    @PostMapping("/items/{id}/toggle")
    public ApiResponse<String> toggleItemStatus(@PathVariable Long id) {
        try {
            return ApiResponse.success(adminItemService.toggleItemStatus(id));
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(404, e.getMessage());
        } catch (Exception e) {
            log.error("切换状态失败", e);
            return ApiResponse.error("切换状态失败");
        }
    }
}
