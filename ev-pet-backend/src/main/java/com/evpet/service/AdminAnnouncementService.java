package com.evpet.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.evpet.mapper.AnnouncementMapper;
import com.evpet.model.Announcement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminAnnouncementService {

    private final AnnouncementMapper announcementMapper;

    public List<Map<String, Object>> getAnnouncements(String type) {
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
        return list;
    }

    public void createAnnouncement(Map<String, Object> data) {
        Announcement a = new Announcement();
        a.setTitle((String) data.get("title"));
        a.setContent((String) data.get("content"));
        a.setType(data.get("type") != null ? (String) data.get("type") : "system");
        a.setTarget(data.get("target") != null ? (String) data.get("target") : "all");
        a.setStatus("draft");
        a.setCreateTime(LocalDateTime.now());
        announcementMapper.insert(a);
    }

    public void updateAnnouncement(Long id, Map<String, Object> data) {
        Announcement a = announcementMapper.selectById(id);
        if (a == null) throw new IllegalArgumentException("公告不存在");
        if (data.containsKey("title")) a.setTitle((String) data.get("title"));
        if (data.containsKey("content")) a.setContent((String) data.get("content"));
        if (data.containsKey("type")) a.setType((String) data.get("type"));
        a.setUpdateTime(LocalDateTime.now());
        announcementMapper.updateById(a);
    }

    public void deleteAnnouncement(Long id) {
        announcementMapper.deleteById(id);
    }

    public void publishAnnouncement(Long id) {
        Announcement a = announcementMapper.selectById(id);
        if (a == null) throw new IllegalArgumentException("公告不存在");
        a.setStatus("published");
        a.setPublishTime(LocalDateTime.now());
        announcementMapper.updateById(a);
    }

    public void withdrawAnnouncement(Long id) {
        Announcement a = announcementMapper.selectById(id);
        if (a == null) throw new IllegalArgumentException("公告不存在");
        a.setStatus("withdrawn");
        announcementMapper.updateById(a);
    }
}
