package com.evpet.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.evpet.mapper.ActivityMapper;
import com.evpet.model.Activity;
import com.evpet.vo.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController extends BaseController {

    private final ActivityMapper activityMapper;

    @GetMapping
    public ApiResponse<List<Activity>> getActivities() {
        try {
            List<Activity> activities = activityMapper.selectList(
                    new LambdaQueryWrapper<Activity>()
                            .in(Activity::getStatus, "active", "upcoming")
                            .orderByDesc(Activity::getCreateTime));
            return ApiResponse.success(activities);
        } catch (Exception e) {
            return ApiResponse.error("获取活动列表失败: " + e.getMessage());
        }
    }
}
