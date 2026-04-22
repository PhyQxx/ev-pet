package com.evpet.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.evpet.mapper.ItemMapper;
import com.evpet.mapper.UserItemMapper;
import com.evpet.mapper.UserMapper;
import com.evpet.model.Item;
import com.evpet.model.User;
import com.evpet.model.UserItem;
import com.evpet.vo.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final UserMapper userMapper;
    private final ItemMapper itemMapper;
    private final UserItemMapper userItemMapper;

    // ============ 统计数据 ============

    @GetMapping("/stats/overview")
    public ApiResponse<Map<String, Object>> getOverview() {
        try {
            long totalUsers = userMapper.selectCount(null);
            long todayActive = userMapper.selectCount(
                    new LambdaQueryWrapper<User>()
                            .ge(User::getUpdateTime, LocalDateTime.now().minusDays(1))
            );

            // 模拟数据（实际项目按需接入支付表）
            Map<String, Object> data = new HashMap<>();
            data.put("totalUsers", totalUsers);
            data.put("todayActive", todayActive);
            data.put("monthActive", totalUsers * 3 / 10);
            data.put("todayRevenue", 3842);
            data.put("todayPayingUsers", 428);
            data.put("arpu", 8.97);
            return ApiResponse.success(data);
        } catch (Exception e) {
            log.error("获取统计概览失败", e);
            return ApiResponse.error("获取统计概览失败: " + e.getMessage());
        }
    }

    @GetMapping("/stats/dau")
    public ApiResponse<List<Map<String, Object>>> getDauTrend(
            @RequestParam(defaultValue = "7") int days) {
        try {
            List<Map<String, Object>> data = new ArrayList<>();
            // 模拟近N日 DAU 数据
            String[] labels = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
            int[] values = {9240, 10120, 11580, 10890, 13200, 14560, 12840};
            int offset = 7 - days;
            for (int i = 0; i < days && i < 7; i++) {
                Map<String, Object> day = new HashMap<>();
                day.put("label", labels[(i + offset) % 7]);
                day.put("value", values[(i + offset) % 7]);
                data.add(day);
            }
            return ApiResponse.success(data);
        } catch (Exception e) {
            return ApiResponse.error("获取DAU趋势失败: " + e.getMessage());
        }
    }

    @GetMapping("/stats/revenue")
    public ApiResponse<List<Map<String, Object>>> getRevenueTrend(
            @RequestParam(defaultValue = "7") int days) {
        try {
            List<Map<String, Object>> data = new ArrayList<>();
            String[] labels = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
            int[] values = {2800, 3200, 3100, 3600, 4200, 5100, 3842};
            int offset = 7 - days;
            for (int i = 0; i < days && i < 7; i++) {
                Map<String, Object> day = new HashMap<>();
                day.put("label", labels[(i + offset) % 7]);
                day.put("value", values[(i + offset) % 7]);
                data.add(day);
            }
            return ApiResponse.success(data);
        } catch (Exception e) {
            return ApiResponse.error("获取收入趋势失败: " + e.getMessage());
        }
    }

    // ============ 用户管理 ============

    @GetMapping("/users")
    public ApiResponse<Map<String, Object>> getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        try {
            Page<User> p = new Page<>(page, pageSize);
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            if (keyword != null && !keyword.isEmpty()) {
                wrapper.like(User::getNickname, keyword)
                        .or().like(User::getOpenId, keyword);
            }
            if ("banned".equals(status)) {
                wrapper.eq(User::getStatus, 0);
            } else if ("active".equals(status)) {
                wrapper.eq(User::getStatus, 1);
            }
            wrapper.orderByDesc(User::getCreateTime);
            Page<User> result = userMapper.selectPage(p, wrapper);

            Map<String, Object> resp = new HashMap<>();
            resp.put("list", result.getRecords());
            resp.put("total", result.getTotal());
            resp.put("page", result.getCurrent());
            resp.put("pageSize", result.getSize());
            return ApiResponse.success(resp);
        } catch (Exception e) {
            return ApiResponse.error("获取用户列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/users/{id}")
    public ApiResponse<User> getUser(@PathVariable Long id) {
        User user = userMapper.selectById(id);
        if (user == null) return ApiResponse.error(404, "用户不存在");
        return ApiResponse.success(user);
    }

    @PutMapping("/users/{id}")
    public ApiResponse<String> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        try {
            User user = userMapper.selectById(id);
            if (user == null) return ApiResponse.error(404, "用户不存在");
            if (data.containsKey("nickname")) user.setNickname((String) data.get("nickname"));
            if (data.containsKey("gold")) user.setGold((Integer) data.get("gold"));
            if (data.containsKey("level")) user.setLevel((Integer) data.get("level"));
            userMapper.updateById(user);
            return ApiResponse.success("更新成功");
        } catch (Exception e) {
            return ApiResponse.error("更新用户失败: " + e.getMessage());
        }
    }

    @PostMapping("/users/{id}/ban")
    public ApiResponse<String> banUser(@PathVariable Long id) {
        try {
            User user = userMapper.selectById(id);
            if (user == null) return ApiResponse.error(404, "用户不存在");
            user.setStatus(0);
            userMapper.updateById(user);
            return ApiResponse.success("禁用成功");
        } catch (Exception e) {
            return ApiResponse.error("禁用用户失败: " + e.getMessage());
        }
    }

    @PostMapping("/users/{id}/unban")
    public ApiResponse<String> unbanUser(@PathVariable Long id) {
        try {
            User user = userMapper.selectById(id);
            if (user == null) return ApiResponse.error(404, "用户不存在");
            user.setStatus(1);
            userMapper.updateById(user);
            return ApiResponse.success("启用成功");
        } catch (Exception e) {
            return ApiResponse.error("启用用户失败: " + e.getMessage());
        }
    }

    // ============ 商品/道具管理 ============

    @GetMapping("/items")
    public ApiResponse<Map<String, Object>> getItems(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int pageSize,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer status) {
        try {
            Page<Item> p = new Page<>(page, pageSize);
            LambdaQueryWrapper<Item> wrapper = new LambdaQueryWrapper<>();
            if (category != null && !category.isEmpty()) {
                wrapper.eq(Item::getCategory, category);
            }
            if (status != null) {
                wrapper.eq(Item::getStatus, status);
            }
            wrapper.orderByDesc(Item::getCreateTime);
            Page<Item> result = itemMapper.selectPage(p, wrapper);

            Map<String, Object> resp = new HashMap<>();
            resp.put("list", result.getRecords());
            resp.put("total", result.getTotal());
            resp.put("page", result.getCurrent());
            resp.put("pageSize", result.getSize());
            return ApiResponse.success(resp);
        } catch (Exception e) {
            return ApiResponse.error("获取道具列表失败: " + e.getMessage());
        }
    }

    @PostMapping("/items")
    public ApiResponse<String> createItem(@RequestBody Item item) {
        try {
            item.setCreateTime(LocalDateTime.now());
            itemMapper.insert(item);
            return ApiResponse.success("创建成功");
        } catch (Exception e) {
            return ApiResponse.error("创建道具失败: " + e.getMessage());
        }
    }

    @PutMapping("/items/{id}")
    public ApiResponse<String> updateItem(@PathVariable Long id, @RequestBody Item data) {
        try {
            Item item = itemMapper.selectById(id);
            if (item == null) return ApiResponse.error(404, "道具不存在");
            if (data.getName() != null) item.setName(data.getName());
            if (data.getDescription() != null) item.setDescription(data.getDescription());
            if (data.getCategory() != null) item.setCategory(data.getCategory());
            if (data.getPrice() != null) item.setPrice(data.getPrice());
            if (data.getRmbPrice() != null) item.setRmbPrice(data.getRmbPrice());
            if (data.getIcon() != null) item.setIcon(data.getIcon());
            if (data.getEffect() != null) item.setEffect(data.getEffect());
            if (data.getIsFree() != null) item.setIsFree(data.getIsFree());
            itemMapper.updateById(item);
            return ApiResponse.success("更新成功");
        } catch (Exception e) {
            return ApiResponse.error("更新道具失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/items/{id}")
    public ApiResponse<String> deleteItem(@PathVariable Long id) {
        try {
            itemMapper.deleteById(id);
            return ApiResponse.success("删除成功");
        } catch (Exception e) {
            return ApiResponse.error("删除道具失败: " + e.getMessage());
        }
    }

    @PostMapping("/items/{id}/toggle")
    public ApiResponse<String> toggleItemStatus(@PathVariable Long id) {
        try {
            Item item = itemMapper.selectById(id);
            if (item == null) return ApiResponse.error(404, "道具不存在");
            item.setStatus(item.getStatus() == 1 ? 0 : 1);
            itemMapper.updateById(item);
            return ApiResponse.success(item.getStatus() == 1 ? "上架成功" : "下架成功");
        } catch (Exception e) {
            return ApiResponse.error("切换状态失败: " + e.getMessage());
        }
    }

    // ============ 公告管理 ============

    @GetMapping("/announcements")
    public ApiResponse<List<Map<String, Object>>> getAnnouncements(
            @RequestParam(required = false) String type) {
        // 模拟数据
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> a1 = new HashMap<>();
        a1.put("id", 1);
        a1.put("title", "五一活动公告");
        a1.put("content", "五一假期期间，登录即可领取限定宠物服装！");
        a1.put("type", "activity");
        a1.put("status", "published");
        a1.put("createTime", "2026-04-15 10:00");
        list.add(a1);
        if (type == null || type.isEmpty()) {
            Map<String, Object> a2 = new HashMap<>();
            a2.put("id", 2);
            a2.put("title", "系统维护通知");
            a2.put("content", "将于4月20日凌晨2点进行系统维护。");
            a2.put("type", "maintenance");
            a2.put("status", "published");
            a2.put("createTime", "2026-04-14 18:00");
            list.add(a2);
        }
        return ApiResponse.success(list);
    }

    @PostMapping("/announcements")
    public ApiResponse<String> createAnnouncement(@RequestBody Map<String, Object> data) {
        return ApiResponse.success("创建成功");
    }

    @PutMapping("/announcements/{id}")
    public ApiResponse<String> updateAnnouncement(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        return ApiResponse.success("更新成功");
    }

    @DeleteMapping("/announcements/{id}")
    public ApiResponse<String> deleteAnnouncement(@PathVariable Long id) {
        return ApiResponse.success("删除成功");
    }

    @PostMapping("/announcements/{id}/publish")
    public ApiResponse<String> publishAnnouncement(@PathVariable Long id) {
        return ApiResponse.success("发布成功");
    }

    @PostMapping("/announcements/{id}/withdraw")
    public ApiResponse<String> withdrawAnnouncement(@PathVariable Long id) {
        return ApiResponse.success("撤回成功");
    }

    // ============ 活动管理 ============

    @GetMapping("/activities")
    public ApiResponse<List<Map<String, Object>>> getActivities() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> act = new HashMap<>();
        act.put("id", 1);
        act.put("name", "五一登录有礼");
        act.put("icon", "🎉");
        act.put("desc", "五一假期登录领取限定服装");
        act.put("startTime", "2026-04-28");
        act.put("endTime", "2026-05-05");
        act.put("status", "active");
        act.put("participants", 3240);
        list.add(act);
        return ApiResponse.success(list);
    }

    @PostMapping("/activities")
    public ApiResponse<String> createActivity(@RequestBody Map<String, Object> data) {
        return ApiResponse.success("创建成功");
    }

    @PutMapping("/activities/{id}")
    public ApiResponse<String> updateActivity(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        return ApiResponse.success("更新成功");
    }

    @DeleteMapping("/activities/{id}")
    public ApiResponse<String> deleteActivity(@PathVariable Long id) {
        return ApiResponse.success("删除成功");
    }

    @PostMapping("/activities/{id}/end")
    public ApiResponse<String> endActivity(@PathVariable Long id) {
        return ApiResponse.success("活动已结束");
    }

    // ============ 内容审核 ============

    @GetMapping("/reviews")
    public ApiResponse<Map<String, Object>> getReviews(
            @RequestParam(defaultValue = "pending") String status) {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> r = new HashMap<>();
        r.put("id", 1);
        r.put("type", "nickname");
        r.put("userId", 100234);
        r.put("userName", "小华");
        r.put("target", "宠物昵称");
        r.put("violation", "破解用户");
        r.put("reason", "含有广告内容");
        r.put("time", "2026-04-15 18:30");
        if ("pending".equals(status)) list.add(r);
        Map<String, Object> resp = new HashMap<>();
        resp.put("list", list);
        resp.put("total", list.size());
        return ApiResponse.success(resp);
    }

    @PostMapping("/reviews/{id}/approve")
    public ApiResponse<String> approveReview(@PathVariable Long id) {
        return ApiResponse.success("已通过");
    }

    @PostMapping("/reviews/{id}/reject")
    public ApiResponse<String> rejectReview(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return ApiResponse.success("已驳回");
    }

    // ============ 管理员账号 ============

    @GetMapping("/admins")
    public ApiResponse<List<Map<String, Object>>> getAdmins() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> admin = new HashMap<>();
        admin.put("id", 1);
        admin.put("name", "裴浩宇");
        admin.put("role", "super_admin");
        admin.put("roleName", "超级管理员");
        admin.put("lastLogin", "今天 18:00");
        admin.put("avatarEmoji", "🐱");
        admin.put("avatarBg", "#FFD5E5");
        admin.put("isSuper", true);
        list.add(admin);
        return ApiResponse.success(list);
    }

    @PostMapping("/admins")
    public ApiResponse<String> createAdmin(@RequestBody Map<String, Object> data) {
        return ApiResponse.success("创建成功");
    }

    @PutMapping("/admins/{id}")
    public ApiResponse<String> updateAdmin(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        return ApiResponse.success("更新成功");
    }

    @DeleteMapping("/admins/{id}")
    public ApiResponse<String> deleteAdmin(@PathVariable Long id) {
        return ApiResponse.success("删除成功");
    }

    // ============ 角色权限 ============

    @GetMapping("/roles")
    public ApiResponse<List<Map<String, Object>>> getRoles() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> role = new HashMap<>();
        role.put("id", 1);
        role.put("name", "超级管理员");
        role.put("desc", "拥有所有权限");
        role.put("userCount", 1);
        role.put("permissions", Arrays.asList("用户管理", "内容审核", "系统配置", "数据统计", "活动管理"));
        list.add(role);
        Map<String, Object> role2 = new HashMap<>();
        role2.put("id", 2);
        role2.put("name", "运营管理员");
        role2.put("desc", "日常运营人员");
        role2.put("userCount", 3);
        role2.put("permissions", Arrays.asList("内容审核", "活动管理", "公告管理"));
        list.add(role2);
        return ApiResponse.success(list);
    }

    @PutMapping("/roles/{id}")
    public ApiResponse<String> updateRole(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        return ApiResponse.success("更新成功");
    }
}
