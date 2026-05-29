package com.evpet.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.evpet.mapper.UserMapper;
import com.evpet.model.User;
import com.evpet.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminAccountService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;

    public Map<String, Object> login(String username, String password) {
        // TODO: 替换为数据库管理员表查询
        if ("admin".equals(username) && "admin123".equals(password)) {
            String token = jwtUtil.generateToken(0L);
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("username", username);
            data.put("role", "super_admin");
            return data;
        }
        return null;
    }

    public List<Map<String, Object>> getAdmins() {
        // TODO: 对接管理员数据库表
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
        return list;
    }

    public void createAdmin(Map<String, Object> data) {
        // TODO: 对接管理员数据库表
    }

    public void updateAdmin(Long id, Map<String, Object> data) {
        // TODO: 对接管理员数据库表
    }

    public void deleteAdmin(Long id) {
        // TODO: 对接管理员数据库表
    }

    public List<Map<String, Object>> getRoles() {
        // TODO: 对接角色权限表
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
        return list;
    }

    public void updateRole(Long id, Map<String, Object> data) {
        // TODO: 对接角色权限表
    }
}
