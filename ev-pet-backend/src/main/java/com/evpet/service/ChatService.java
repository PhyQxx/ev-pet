package com.evpet.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.evpet.dto.ChatDTO;
import com.evpet.mapper.ChatMessageMapper;
import com.evpet.mapper.PetMapper;
import com.evpet.model.ChatMessage;
import com.evpet.model.Pet;
import com.evpet.utils.ContentFilterUtil;
import com.evpet.vo.ApiResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatMessageMapper chatMessageMapper;
    private final PetMapper petMapper;
    private final ContentFilterUtil contentFilter;

    @Value("${minimax.api-url}")
    private String apiUrl;

    @Value("${minimax.api-key}")
    private String apiKey;

    @Value("${minimax.group-id}")
    private String groupId;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final int MAX_HISTORY = 10;

    @Transactional
    public ApiResponse<String> chat(Long userId, ChatDTO dto) {
        // 安全检查
        if (!contentFilter.isContentSafe(dto.getContent())) {
            return ApiResponse.error("内容包含敏感词，请修改后重试");
        }

        String safeContent = contentFilter.sanitize(dto.getContent());

        // 保存用户消息
        Pet pet = petMapper.selectOne(new LambdaQueryWrapper<Pet>()
                .eq(Pet::getUserId, userId));
        saveMessage(userId, pet.getId(), "user", safeContent);

        // 构建上下文
        String context = buildContext(pet);
        List<ChatMessage> history = getChatHistory(userId, pet.getId());

        // 调用AI
        String aiResponse = callMiniMax(context, history, safeContent);

        // 保存AI回复
        saveMessage(userId, pet.getId(), "assistant", aiResponse);

        return ApiResponse.success(aiResponse);
    }

    public List<ChatMessage> getChatHistory(Long userId, Long petId) {
        return chatMessageMapper.selectList(
                new LambdaQueryWrapper<ChatMessage>()
                        .eq(ChatMessage::getUserId, userId)
                        .eq(ChatMessage::getPetId, petId)
                        .orderByAsc(ChatMessage::getCreateTime)
                        .last("LIMIT " + MAX_HISTORY)
        );
    }

    private void saveMessage(Long userId, Long petId, String role, String content) {
        ChatMessage message = new ChatMessage();
        message.setUserId(userId);
        message.setPetId(petId);
        message.setRole(role);
        message.setContent(content);
        message.setCreateTime(LocalDateTime.now());
        chatMessageMapper.insert(message);
    }

    private String buildContext(Pet pet) {
        String status = getPetStatusDescription(pet);
        return String.format(
                "你是一只可爱的二次元宠物，名字叫%s，当前是第%d阶段，等级%d。%s。",
                pet.getName(), pet.getStage(), pet.getLevel(), status
        );
    }

    private String getPetStatusDescription(Pet pet) {
        if (pet.getHealth() < 30) return "有点累了，想休息";
        if (pet.getFullness() < 30) return "肚子饿了，想吃东西";
        if (pet.getMood() < 30) return "心情不好，想让你陪陪";
        return "很开心，想和你聊天";
    }

    private String callMiniMax(String context, List<ChatMessage> history, String userInput) {
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            // 构建请求体
            String requestBody = buildRequestBody(context, history, userInput);

            Request request = new Request.Builder()
                    .url(apiUrl)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody, JSON))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    return parseAiResponse(responseBody);
                }
            }
        } catch (Exception e) {
            log.error("MiniMax API调用失败", e);
        }

        // 降级返回默认回复
        return getDefaultResponse(userInput);
    }

    private String buildRequestBody(String context, List<ChatMessage> history, String userInput) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"model\":\"MiniMax-Text-01\",\"messages\":[");
        sb.append("{\"role\":\"system\",\"content\":\"").append(escapeJson(context)).append("\"},");

        for (int i = 0; i < history.size(); i++) {
            ChatMessage msg = history.get(i);
            sb.append("{\"role\":\"").append(msg.getRole())
                    .append("\",\"content\":\"").append(escapeJson(msg.getContent())).append("\"}");
            if (i < history.size() - 1) sb.append(",");
        }

        sb.append("],\"stream\":false}");
        return sb.toString();
    }

    private String parseAiResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            return root.path("choices").path(0).path("messages").path(0).path("content").asText();
        } catch (Exception e) {
            log.error("解析AI响应失败", e);
            return getDefaultResponse("");
        }
    }

    private String getDefaultResponse(String input) {
        if (input.contains("饿") || input.contains("吃的")) {
            return "主人，我有点饿了，能给我喂点吃的吗？🍖";
        } else if (input.contains("累") || input.contains("困")) {
            return "有点困了呢，让我休息一下吧~ 💤";
        } else if (input.contains("开心") || input.contains("高兴")) {
            return "看到主人开心我也好开心呀！✨";
        }
        return "主人你好呀！今天想和我聊什么呢？🐾";
    }

    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
