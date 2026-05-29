package com.evpet.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.evpet.mapper.ContentReviewMapper;
import com.evpet.model.ContentReview;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminContentService {

    private final ContentReviewMapper contentReviewMapper;

    public Map<String, Object> getReviews(String status) {
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
        return resp;
    }

    public void approveReview(Long id) {
        ContentReview r = contentReviewMapper.selectById(id);
        if (r == null) throw new IllegalArgumentException("审核项不存在");
        r.setStatus("approved");
        r.setReviewTime(LocalDateTime.now());
        contentReviewMapper.updateById(r);
    }

    public void rejectReview(Long id, String reason) {
        ContentReview r = contentReviewMapper.selectById(id);
        if (r == null) throw new IllegalArgumentException("审核项不存在");
        r.setStatus("rejected");
        r.setReason(reason);
        r.setReviewTime(LocalDateTime.now());
        contentReviewMapper.updateById(r);
    }
}
