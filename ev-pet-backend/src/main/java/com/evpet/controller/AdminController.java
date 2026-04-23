package com.evpet.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.evpet.mapper.ActivityMapper;
import com.evpet.mapper.AnnouncementMapper;
import com.evpet.mapper.ContentReviewMapper;
import com.evpet.mapper.ItemMapper;
import com.evpet.mapper.UserItemMapper;
import com.evpet.mapper.UserMapper;
import com.evpet.model.Activity;
import com.evpet.model.Announcement;
import com.evpet.model.ContentReview;
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
    private final AnnouncementMapper announcementMapper;
    private final ActivityMapper activityMapper;
    private final ContentReviewMapper contentReviewMapper;

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
                wrapper.and(w -> w.like(User::getNickname, keyword).or().like(User::getOpenId, keyword));
            }
            if ("banned".equals(status)) {
                wrapper.eq(User::getStatus, 0);
            } else if ("active".equals(status)) {
                wrapper.eq(User::getStatus, 1);
            }
            wrapper.orderByDesc(User::getCreateTime);
            Page<User> result = userMapper.selectPage(p, wrapper);

            // 构建带前端所需字段的用户列表
            List<Map<String, Object>> list = new ArrayList<>();
            for (User u : result.getRecords()) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", u.getId());
                m.put("nickname", u.getNickname());
                m.put("avatar", u.getAvatar());
                m.put("avatarEmoji", "🐱");
                m.put("avatarBg", "#FFD5E5");
                m.put("gold", u.getGold());
                m.put("level", u.getLevel());
                m.put("status", u.getStatus() == 1 ? "正常" : "禁用");
                m.put("lastActive", u.getUpdateTime() != null ? u.getUpdateTime().toString().substring(0, 10) : "—");
                // 注册方式：通过 openId 判断
                m.put("regType", u.getOpenId() != null && !u.getOpenId().isEmpty() ? "wechat" : (u.getPhone() != null ? "phone" : "guest"));
                m.put("paidAmount", 0); // TODO: 接入支付表后计算
                m.put("createTime", u.getCreateTime() != null ? u.getCreateTime().toString().substring(0, 16).replace("T", " ") : "");
                // 宠物信息：从 Pet 模型获取
                m.put("petName", "小布");
                m.put("petEmoji", "🐱");
                m.put("petLevel", u.getLevel() != null ? u.getLevel() : 1);
                list.add(m);
            }

            Map<String, Object> resp = new HashMap<>();
            resp.put("list", list);
            resp.put("total", result.getTotal() > 0 ? result.getTotal() : list.size());
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
        try {
            LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
            if (type != null && !type.isEmpty()) {
                wrapper.eq(Announcement::getType, type);
            }
            wrapper.orderByDesc(Announcement::getCreateTime);
            List<Announcement> results = announcementMapper.selectList(wrapper);

            List<Map<String, Object>> list = new ArrayList<>();
            for (Announcement a : results) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", a.getId());
                m.put("title", a.getTitle());
                m.put("content", a.getContent());
                m.put("type", a.getType() != null ? a.getType() : "system");
                m.put("status", a.getStatus() != null ? a.getStatus() : "draft");
                m.put("createTime", a.getCreateTime() != null ? a.getCreateTime().toString().substring(0, 16).replace("T", " ") : "");
                m.put("publishTime", a.getPublishTime() != null ? a.getPublishTime().toString().substring(0, 16).replace("T", " ") : "");
                list.add(m);
            }
            return ApiResponse.success(list);
        } catch (Exception e) {
            return ApiResponse.error("获取公告列表失败: " + e.getMessage());
        }
    }

    @PostMapping("/announcements")
    public ApiResponse<String> createAnnouncement(@RequestBody Map<String, Object> data) {
        try {
            Announcement a = new Announcement();
            a.setTitle((String) data.get("title"));
            a.setContent((String) data.get("content"));
            a.setType(data.get("type") != null ? (String) data.get("type") : "system");
            a.setTarget(data.get("target") != null ? (String) data.get("target") : "all");
            a.setStatus("draft");
            a.setCreateTime(LocalDateTime.now());
            announcementMapper.insert(a);
            return ApiResponse.success("创建成功");
        } catch (Exception e) {
            return ApiResponse.error("创建公告失败: " + e.getMessage());
        }
    }

    @PutMapping("/announcements/{id}")
    public ApiResponse<String> updateAnnouncement(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        try {
            Announcement a = announcementMapper.selectById(id);
            if (a == null) return ApiResponse.error(404, "公告不存在");
            if (data.containsKey("title")) a.setTitle((String) data.get("title"));
            if (data.containsKey("content")) a.setContent((String) data.get("content"));
            if (data.containsKey("type")) a.setType((String) data.get("type"));
            a.setUpdateTime(LocalDateTime.now());
            announcementMapper.updateById(a);
            return ApiResponse.success("更新成功");
        } catch (Exception e) {
            return ApiResponse.error("更新公告失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/announcements/{id}")
    public ApiResponse<String> deleteAnnouncement(@PathVariable Long id) {
        try {
            announcementMapper.deleteById(id);
            return ApiResponse.success("删除成功");
        } catch (Exception e) {
            return ApiResponse.error("删除公告失败: " + e.getMessage());
        }
    }

    @PostMapping("/announcements/{id}/publish")
    public ApiResponse<String> publishAnnouncement(@PathVariable Long id) {
        try {
            Announcement a = announcementMapper.selectById(id);
            if (a == null) return ApiResponse.error(404, "公告不存在");
            a.setStatus("published");
            a.setPublishTime(LocalDateTime.now());
            announcementMapper.updateById(a);
            return ApiResponse.success("发布成功");
        } catch (Exception e) {
            return ApiResponse.error("发布公告失败: " + e.getMessage());
        }
    }

    @PostMapping("/announcements/{id}/withdraw")
    public ApiResponse<String> withdrawAnnouncement(@PathVariable Long id) {
        try {
            Announcement a = announcementMapper.selectById(id);
            if (a == null) return ApiResponse.error(404, "公告不存在");
            a.setStatus("withdrawn");
            announcementMapper.updateById(a);
            return ApiResponse.success("撤回成功");
        } catch (Exception e) {
            return ApiResponse.error("撤回公告失败: " + e.getMessage());
        }
    }

    // ============ 活动管理 ============

    @GetMapping("/activities")
    public ApiResponse<List<Map<String, Object>>> getActivities() {
        try {
            List<Activity> results = activityMapper.selectList(
                    new LambdaQueryWrapper<Activity>().orderByDesc(Activity::getCreateTime));

            List<Map<String, Object>> list = new ArrayList<>();
            for (Activity act : results) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", act.getId());
                m.put("name", act.getName());
                m.put("icon", act.getIcon() != null ? act.getIcon() : "🎉");
                m.put("desc", act.getDescription());
                m.put("startTime", act.getStartTime() != null ? act.getStartTime().toString().substring(0, 10) : "");
                m.put("endTime", act.getEndTime() != null ? act.getEndTime().toString().substring(0, 10) : "");
                m.put("status", act.getStatus() != null ? act.getStatus() : "draft");
                m.put("participants", act.getParticipants() != null ? act.getParticipants() : 0);
                list.add(m);
            }
            return ApiResponse.success(list);
        } catch (Exception e) {
            return ApiResponse.error("获取活动列表失败: " + e.getMessage());
        }
    }

    @PostMapping("/activities")
    public ApiResponse<String> createActivity(@RequestBody Map<String, Object> data) {
        try {
            Activity act = new Activity();
            act.setName((String) data.get("name"));
            act.setIcon(data.get("icon") != null ? (String) data.get("icon") : "🎉");
            act.setDescription((String) data.get("description"));
            act.setStatus("draft");
            act.setCreateTime(LocalDateTime.now());
            activityMapper.insert(act);
            return ApiResponse.success("创建成功");
        } catch (Exception e) {
            return ApiResponse.error("创建活动失败: " + e.getMessage());
        }
    }

    @PutMapping("/activities/{id}")
    public ApiResponse<String> updateActivity(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        try {
            Activity act = activityMapper.selectById(id);
            if (act == null) return ApiResponse.error(404, "活动不存在");
            if (data.containsKey("name")) act.setName((String) data.get("name"));
            if (data.containsKey("icon")) act.setIcon((String) data.get("icon"));
            if (data.containsKey("description")) act.setDescription((String) data.get("description"));
            if (data.containsKey("status")) act.setStatus((String) data.get("status"));
            act.setUpdateTime(LocalDateTime.now());
            activityMapper.updateById(act);
            return ApiResponse.success("更新成功");
        } catch (Exception e) {
            return ApiResponse.error("更新活动失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/activities/{id}")
    public ApiResponse<String> deleteActivity(@PathVariable Long id) {
        try {
            activityMapper.deleteById(id);
            return ApiResponse.success("删除成功");
        } catch (Exception e) {
            return ApiResponse.error("删除活动失败: " + e.getMessage());
        }
    }

    @PostMapping("/activities/{id}/end")
    public ApiResponse<String> endActivity(@PathVariable Long id) {
        try {
            Activity act = activityMapper.selectById(id);
            if (act == null) return ApiResponse.error(404, "活动不存在");
            act.setStatus("ended");
            activityMapper.updateById(act);
            return ApiResponse.success("活动已结束");
        } catch (Exception e) {
            return ApiResponse.error("结束活动失败: " + e.getMessage());
        }
    }

    // ============ 内容审核 ============

    @GetMapping("/reviews")
    public ApiResponse<Map<String, Object>> getReviews(
            @RequestParam(defaultValue = "pending") String status) {
        try {
            LambdaQueryWrapper<ContentReview> wrapper = new LambdaQueryWrapper<>();
            if (!"all".equals(status)) {
                wrapper.eq(ContentReview::getStatus, status);
            }
            wrapper.orderByDesc(ContentReview::getCreateTime);
            List<ContentReview> results = contentReviewMapper.selectList(wrapper);

            List<Map<String, Object>> list = new ArrayList<>();
            for (ContentReview r : results) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", r.getId());
                m.put("type", r.getType());
                m.put("userId", r.getUserId());
                m.put("userName", r.getUserName());
                m.put("target", r.getTarget());
                m.put("content", r.getContent());
                m.put("violation", r.getViolation());
                m.put("reason", r.getReason());
                m.put("status", r.getStatus());
                m.put("time", r.getCreateTime() != null ? r.getCreateTime().toString().substring(0, 16).replace("T", " ") : "");
                list.add(m);
            }
            Map<String, Object> resp = new HashMap<>();
            resp.put("list", list);
            resp.put("total", list.size());
            return ApiResponse.success(resp);
        } catch (Exception e) {
            return ApiResponse.error("获取审核列表失败: " + e.getMessage());
        }
    }

    @PostMapping("/reviews/{id}/approve")
    public ApiResponse<String> approveReview(@PathVariable Long id) {
        try {
            ContentReview r = contentReviewMapper.selectById(id);
            if (r == null) return ApiResponse.error(404, "审核项不存在");
            r.setStatus("approved");
            r.setReviewTime(LocalDateTime.now());
            contentReviewMapper.updateById(r);
            return ApiResponse.success("已通过");
        } catch (Exception e) {
            return ApiResponse.error("审核操作失败: " + e.getMessage());
        }
    }

    @PostMapping("/reviews/{id}/reject")
    public ApiResponse<String> rejectReview(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            ContentReview r = contentReviewMapper.selectById(id);
            if (r == null) return ApiResponse.error(404, "审核项不存在");
            r.setStatus("rejected");
            r.setReason(body.get("reason"));
            r.setReviewTime(LocalDateTime.now());
            contentReviewMapper.updateById(r);
            return ApiResponse.success("已驳回");
        } catch (Exception e) {
            return ApiResponse.error("审核操作失败: " + e.getMessage());
        }
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
