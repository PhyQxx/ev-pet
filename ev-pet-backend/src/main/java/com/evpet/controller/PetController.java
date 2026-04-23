package com.evpet.controller;

import com.evpet.service.PetService;
import com.evpet.vo.ApiResponse;
import com.evpet.vo.PetVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pet")
@RequiredArgsConstructor
public class PetController extends BaseController {

    private final PetService petService;

    @GetMapping("/info")
    public ApiResponse<PetVO> getPetInfo(@RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            PetVO pet = petService.getPetInfo(userId);
            return ApiResponse.success(pet);
        } catch (Exception e) {
            return ApiResponse.error("获取宠物信息失败: " + e.getMessage());
        }
    }

    @PostMapping("/feed")
    public ApiResponse<PetVO> feed(@RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            PetVO pet = petService.feed(userId);
            return ApiResponse.success(pet);
        } catch (Exception e) {
            return ApiResponse.error("喂食失败: " + e.getMessage());
        }
    }

    @PostMapping("/bath")
    public ApiResponse<PetVO> bath(@RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            PetVO pet = petService.bath(userId);
            return ApiResponse.success(pet);
        } catch (Exception e) {
            return ApiResponse.error("洗澡失败: " + e.getMessage());
        }
    }

    @PostMapping("/play")
    public ApiResponse<PetVO> play(@RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            PetVO pet = petService.play(userId);
            return ApiResponse.success(pet);
        } catch (Exception e) {
            return ApiResponse.error("陪玩失败: " + e.getMessage());
        }
    }

    @PutMapping("/name")
    public ApiResponse<PetVO> updateName(
            @RequestHeader("Authorization") String token,
            @RequestParam String name) {
        try {
            Long userId = getUserIdFromToken(token);
            PetVO pet = petService.updateName(userId, name);
            return ApiResponse.success(pet);
        } catch (Exception e) {
            return ApiResponse.error("修改名字失败: " + e.getMessage());
        }
    }

}
