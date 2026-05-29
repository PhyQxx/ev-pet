package com.evpet.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.evpet.mapper.UserMapper;
import com.evpet.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminUserService {

    private final UserMapper userMapper;

    public Map<String, Object> getUsers(int page, int pageSize, String keyword, String status, String type) {
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
        if ("wechat".equals(type)) {
            wrapper.isNotNull(User::getOpenId).ne(User::getOpenId, "");
        } else if ("phone".equals(type)) {
            wrapper.and(w -> w.isNull(User::getOpenId).or().eq(User::getOpenId, ""))
                  .isNotNull(User::getPhone);
        } else if ("guest".equals(type)) {
            wrapper.and(w -> w.isNull(User::getOpenId).or().eq(User::getOpenId, ""))
                  .and(w -> w.isNull(User::getPhone).or().eq(User::getPhone, ""));
        }
        wrapper.orderByDesc(User::getCreateTime);
        Page<User> result = userMapper.selectPage(p, wrapper);

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
            m.put("regType", u.getOpenId() != null && !u.getOpenId().isEmpty() ? "wechat" : (u.getPhone() != null ? "phone" : "guest"));
            m.put("paidAmount", 0);
            m.put("createTime", u.getCreateTime() != null ? u.getCreateTime().toString().substring(0, 16).replace("T", " ") : "");
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
        return resp;
    }

    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    public void updateUser(Long id, Map<String, Object> data) {
        User user = userMapper.selectById(id);
        if (user == null) throw new IllegalArgumentException("用户不存在");
        if (data.containsKey("nickname")) user.setNickname((String) data.get("nickname"));
        if (data.containsKey("gold")) user.setGold((Integer) data.get("gold"));
        if (data.containsKey("level")) user.setLevel((Integer) data.get("level"));
        userMapper.updateById(user);
    }

    public void banUser(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) throw new IllegalArgumentException("用户不存在");
        user.setStatus(0);
        userMapper.updateById(user);
    }

    public void unbanUser(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) throw new IllegalArgumentException("用户不存在");
        user.setStatus(1);
        userMapper.updateById(user);
    }
}
