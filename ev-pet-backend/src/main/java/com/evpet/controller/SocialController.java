package com.evpet.controller;

import com.evpet.service.SocialService;
import com.evpet.vo.ApiResponse;
import com.evpet.vo.SocialVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/social")
@RequiredArgsConstructor
public class SocialController extends BaseController {

    private final SocialService socialService;

    @GetMapping("/friends")
    public ApiResponse<SocialVO> getFriends(@RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            return ApiResponse.success(socialService.getFriends(userId));
        } catch (Exception e) {
            return ApiResponse.error("获取好友列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/posts")
    public ApiResponse<SocialVO> getPosts(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "recent") String filter) {
        try {
            Long userId = getUserIdFromToken(token);
            return ApiResponse.success(socialService.getPosts(userId, filter));
        } catch (Exception e) {
            return ApiResponse.error("获取动态失败: " + e.getMessage());
        }
    }

    @GetMapping("/rankings")
    public ApiResponse<SocialVO> getRankings(@RequestParam(defaultValue = "level") String type) {
        try {
            return ApiResponse.success(socialService.getRankings(type));
        } catch (Exception e) {
            return ApiResponse.error("获取排行榜失败: " + e.getMessage());
        }
    }

    @PostMapping("/friend/add")
    public ApiResponse<String> addFriend(
            @RequestHeader("Authorization") String token,
            @RequestParam Long friendId) {
        try {
            Long userId = getUserIdFromToken(token);
            return socialService.addFriend(userId, friendId);
        } catch (Exception e) {
            return ApiResponse.error("添加好友失败: " + e.getMessage());
        }
    }

    @PostMapping("/friend/accept")
    public ApiResponse<String> acceptFriend(
            @RequestHeader("Authorization") String token,
            @RequestParam Long requestId) {
        try {
            Long userId = getUserIdFromToken(token);
            return socialService.acceptFriend(userId, requestId);
        } catch (Exception e) {
            return ApiResponse.error("接受好友请求失败: " + e.getMessage());
        }
    }

    @PostMapping("/friend/reject")
    public ApiResponse<String> rejectFriend(
            @RequestHeader("Authorization") String token,
            @RequestParam Long requestId) {
        try {
            Long userId = getUserIdFromToken(token);
            return socialService.rejectFriend(userId, requestId);
        } catch (Exception e) {
            return ApiResponse.error("拒绝好友请求失败: " + e.getMessage());
        }
    }

    @PostMapping("/post/publish")
    public ApiResponse<String> publishPost(
            @RequestHeader("Authorization") String token,
            @RequestParam String content) {
        try {
            Long userId = getUserIdFromToken(token);
            return socialService.publishPost(userId, content);
        } catch (Exception e) {
            return ApiResponse.error("发布动态失败: " + e.getMessage());
        }
    }

    @PostMapping("/post/like")
    public ApiResponse<String> likePost(
            @RequestHeader("Authorization") String token,
            @RequestParam Long postId) {
        try {
            Long userId = getUserIdFromToken(token);
            return socialService.likePost(userId, postId);
        } catch (Exception e) {
            return ApiResponse.error("点赞失败: " + e.getMessage());
        }
    }

}
