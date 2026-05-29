package com.evpet.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.evpet.mapper.UserMapper;
import com.evpet.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminStatsService {

    private final UserMapper userMapper;

    public Map<String, Object> getOverview() {
        long totalUsers = userMapper.selectCount(null);
        long todayActive = userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .ge(User::getUpdateTime, LocalDateTime.now().minusDays(1)));

        Map<String, Object> data = new HashMap<>();
        data.put("totalUsers", totalUsers);
        data.put("todayActive", todayActive);
        data.put("monthActive", totalUsers * 3 / 10);
        data.put("todayRevenue", 3842);
        data.put("todayPayingUsers", 428);
        data.put("arpu", 8.97);
        return data;
    }

    public List<Map<String, Object>> getDauTrend(int days) {
        List<Map<String, Object>> data = new ArrayList<>();
        String[] labels = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        int[] values = {9240, 10120, 11580, 10890, 13200, 14560, 12840};
        int offset = 7 - days;
        for (int i = 0; i < days && i < 7; i++) {
            Map<String, Object> day = new HashMap<>();
            day.put("label", labels[(i + offset) % 7]);
            day.put("value", values[(i + offset) % 7]);
            data.add(day);
        }
        return data;
    }

    public List<Map<String, Object>> getRevenueTrend(int days) {
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
        return data;
    }
}
