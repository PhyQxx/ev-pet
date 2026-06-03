package com.evpet.controller;

import com.evpet.mapper.UserMapper;
import com.evpet.model.User;
import com.evpet.vo.ApiResponse;
import com.evpet.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController extends BaseController {

    private final UserMapper userMapper;

    @GetMapping("/profile")
    public ApiResponse<UserVO> getProfile(@RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            User user = userMapper.selectById(userId);
            if (user == null) {
                return ApiResponse.error("用户不存在");
            }
            UserVO vo = UserVO.builder()
                    .id(user.getId())
                    .nickname(user.getNickname())
                    .avatar(user.getAvatar())
                    .gold(user.getGold())
                    .level(user.getLevel())
                    .exp(user.getExp())
                    .build();
            return ApiResponse.success(vo);
        } catch (Exception e) {
            return ApiResponse.error("获取用户信息失败: " + e.getMessage());
        }
    }

    @PutMapping("/profile")
    public ApiResponse<UserVO> updateProfile(
            @RequestHeader("Authorization") String token,
            @RequestBody UserUpdateRequest request) {
        try {
            Long userId = getUserIdFromToken(token);
            User user = userMapper.selectById(userId);
            if (user == null) {
                return ApiResponse.error("用户不存在");
            }
            if (request.getNickname() != null && !request.getNickname().isBlank()) {
                user.setNickname(request.getNickname());
            }
            if (request.getAvatar() != null) {
                user.setAvatar(request.getAvatar());
            }
            user.setUpdateTime(LocalDateTime.now());
            userMapper.updateById(user);

            UserVO vo = UserVO.builder()
                    .id(user.getId())
                    .nickname(user.getNickname())
                    .avatar(user.getAvatar())
                    .gold(user.getGold())
                    .level(user.getLevel())
                    .exp(user.getExp())
                    .build();
            return ApiResponse.success(vo);
        } catch (Exception e) {
            return ApiResponse.error("更新用户信息失败: " + e.getMessage());
        }
    }

    @lombok.Data
    public static class UserUpdateRequest {
        private String nickname;
        private String avatar;
    }
}
