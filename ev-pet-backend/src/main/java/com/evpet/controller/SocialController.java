package com.evpet.controller;

import com.evpet.service.SocialService;
import com.evpet.vo.ApiResponse;
import com.evpet.vo.SocialVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
            return ApiResponse.success(socialService.addFriend(userId, friendId));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("添加好友失败");
        }
    }

    @PostMapping("/friend/accept")
    public ApiResponse<String> acceptFriend(
            @RequestHeader("Authorization") String token,
            @RequestParam Long requestId) {
        try {
            Long userId = getUserIdFromToken(token);
            return ApiResponse.success(socialService.acceptFriend(userId, requestId));
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("接受好友请求失败");
        }
    }

    @PostMapping("/friend/reject")
    public ApiResponse<String> rejectFriend(
            @RequestHeader("Authorization") String token,
            @RequestParam Long requestId) {
        try {
            Long userId = getUserIdFromToken(token);
            return ApiResponse.success(socialService.rejectFriend(userId, requestId));
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("拒绝好友请求失败");
        }
    }

    @PostMapping("/post/publish")
    public ApiResponse<String> publishPost(
            @RequestHeader("Authorization") String token,
            @RequestParam String content) {
        try {
            Long userId = getUserIdFromToken(token);
            return ApiResponse.success(socialService.publishPost(userId, content));
        } catch (Exception e) {
            return ApiResponse.error("发布动态失败");
        }
    }

    @PostMapping("/post/like")
    public ApiResponse<String> likePost(
            @RequestHeader("Authorization") String token,
            @RequestParam Long postId) {
        try {
            Long userId = getUserIdFromToken(token);
            return ApiResponse.success(socialService.likePost(userId, postId));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("点赞失败");
        }
    }

    @PostMapping("/post/comment")
    public ApiResponse<String> addComment(
            @RequestHeader("Authorization") String token,
            @RequestParam Long postId,
            @RequestParam String content) {
        try {
            Long userId = getUserIdFromToken(token);
            return ApiResponse.success(socialService.addComment(userId, postId, content));
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("评论失败");
        }
    }

    @GetMapping("/post/comments")
    public ApiResponse<List<Map<String, Object>>> getComments(@RequestParam Long postId) {
        try {
            return ApiResponse.success(socialService.getComments(postId));
        } catch (Exception e) {
            return ApiResponse.error("获取评论失败");
        }
    }

}
