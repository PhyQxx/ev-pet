package com.evpet.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.evpet.mapper.ActivityMapper;
import com.evpet.model.Activity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminActivityService {

    private final ActivityMapper activityMapper;

    public List<Map<String, Object>> getActivities() {
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
        return list;
    }

    public void createActivity(Map<String, Object> data) {
        Activity act = new Activity();
        act.setName((String) data.get("name"));
        act.setIcon(data.get("icon") != null ? (String) data.get("icon") : "🎉");
        act.setDescription((String) data.get("description"));
        act.setStatus("draft");
        act.setCreateTime(LocalDateTime.now());
        activityMapper.insert(act);
    }

    public void updateActivity(Long id, Map<String, Object> data) {
        Activity act = activityMapper.selectById(id);
        if (act == null) throw new IllegalArgumentException("活动不存在");
        if (data.containsKey("name")) act.setName((String) data.get("name"));
        if (data.containsKey("icon")) act.setIcon((String) data.get("icon"));
        if (data.containsKey("description")) act.setDescription((String) data.get("description"));
        if (data.containsKey("status")) act.setStatus((String) data.get("status"));
        act.setUpdateTime(LocalDateTime.now());
        activityMapper.updateById(act);
    }

    public void deleteActivity(Long id) {
        activityMapper.deleteById(id);
    }

    public void endActivity(Long id) {
        Activity act = activityMapper.selectById(id);
        if (act == null) throw new IllegalArgumentException("活动不存在");
        act.setStatus("ended");
        activityMapper.updateById(act);
    }
}
