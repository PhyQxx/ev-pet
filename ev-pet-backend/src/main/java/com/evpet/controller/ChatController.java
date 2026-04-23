package com.evpet.controller;

import com.evpet.dto.ChatDTO;
import com.evpet.model.ChatMessage;
import com.evpet.service.ChatService;
import com.evpet.vo.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController extends BaseController {

    private final ChatService chatService;

    @PostMapping("/send")
    public ApiResponse<String> send(
            @RequestHeader("Authorization") String token,
            @RequestBody ChatDTO dto) {
        try {
            Long userId = getUserIdFromToken(token);
            return chatService.chat(userId, dto);
        } catch (Exception e) {
            return ApiResponse.error("发送消息失败: " + e.getMessage());
        }
    }

    @GetMapping("/history")
    public ApiResponse<List<ChatMessage>> getHistory(
            @RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            return ApiResponse.success(chatService.getChatHistory(userId));
        } catch (Exception e) {
            return ApiResponse.error("获取历史记录失败: " + e.getMessage());
        }
    }
}
