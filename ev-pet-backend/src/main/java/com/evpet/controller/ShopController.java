package com.evpet.controller;

import com.evpet.mapper.PetMapper;
import com.evpet.model.Item;
import com.evpet.model.Pet;
import com.evpet.model.UserItem;
import com.evpet.service.ShopService;
import com.evpet.vo.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
public class ShopController extends BaseController {

    private final ShopService shopService;
    private final PetMapper petMapper;

    @GetMapping("/items")
    public ApiResponse<List<Item>> getItems(@RequestParam(required = false) String category) {
        try {
            List<Item> items = category != null 
                    ? shopService.getItemsByCategory(category) 
                    : shopService.getAllItems();
            return ApiResponse.success(items);
        } catch (Exception e) {
            return ApiResponse.error("获取道具列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/free")
    public ApiResponse<List<Item>> getFreeItems() {
        try {
            List<Item> items = shopService.getFreeItems();
            return ApiResponse.success(items);
        } catch (Exception e) {
            return ApiResponse.error("获取免费道具失败: " + e.getMessage());
        }
    }

    @GetMapping("/backpack")
    public ApiResponse<Map<String, Object>> getBackpack(@RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            Map<String, Object> backpack = shopService.getUserBackpack(userId);
            return ApiResponse.success(backpack);
        } catch (Exception e) {
            return ApiResponse.error("获取背包失败: " + e.getMessage());
        }
    }

    @PostMapping("/buy")
    public ApiResponse<String> buy(
            @RequestHeader("Authorization") String token,
            @RequestParam Long itemId,
            @RequestParam(required = false, defaultValue = "1") Integer quantity) {
        try {
            Long userId = getUserIdFromToken(token);
            return shopService.buyItem(userId, itemId, quantity);
        } catch (Exception e) {
            return ApiResponse.error("购买失败: " + e.getMessage());
        }
    }

    @PostMapping("/equip")
    public ApiResponse<String> equip(
            @RequestHeader("Authorization") String token,
            @RequestParam Long itemId) {
        try {
            Long userId = getUserIdFromToken(token);
            return shopService.equipItem(userId, itemId);
        } catch (Exception e) {
            return ApiResponse.error("穿戴失败: " + e.getMessage());
        }
    }

    @PostMapping("/use")
    public ApiResponse<String> use(
            @RequestHeader("Authorization") String token,
            @RequestParam Long itemId) {
        try {
            Long userId = getUserIdFromToken(token);
            return shopService.useItem(userId, itemId);
        } catch (Exception e) {
            return ApiResponse.error("使用道具失败: " + e.getMessage());
        }
    }

}
