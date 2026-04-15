package com.evpet.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.evpet.mapper.ItemMapper;
import com.evpet.mapper.UserItemMapper;
import com.evpet.mapper.UserMapper;
import com.evpet.model.Item;
import com.evpet.model.User;
import com.evpet.model.UserItem;
import com.evpet.vo.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ItemMapper itemMapper;
    private final UserItemMapper userItemMapper;
    private final UserMapper userMapper;

    public List<Item> getItemsByCategory(String category) {
        LambdaQueryWrapper<Item> wrapper = new LambdaQueryWrapper<>();
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Item::getCategory, category);
        }
        wrapper.orderByAsc(Item::getPrice);
        return itemMapper.selectList(wrapper);
    }

    public List<Item> getAllItems() {
        return itemMapper.selectList(new LambdaQueryWrapper<Item>()
                .orderByAsc(Item::getCategory, Item::getPrice));
    }

    public List<Item> getFreeItems() {
        return itemMapper.selectList(new LambdaQueryWrapper<Item>()
                .eq(Item::getIsFree, true)
                .orderByAsc(Item::getPrice));
    }

    @Transactional
    public ApiResponse<String> buyItem(Long userId, Long itemId, Integer quantity) {
        quantity = quantity == null ? 1 : quantity;

        Item item = itemMapper.selectById(itemId);
        if (item == null) {
            return ApiResponse.error("道具不存在");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            return ApiResponse.error("用户不存在");
        }

        int totalPrice = item.getPrice() * quantity;
        if (user.getGold() < totalPrice) {
            return ApiResponse.error("金币不足，需要" + totalPrice + "金币");
        }

        // 扣除金币
        user.setGold(user.getGold() - totalPrice);
        userMapper.updateById(user);

        // 添加道具到用户背包
        UserItem userItem = userItemMapper.selectOne(new LambdaQueryWrapper<UserItem>()
                .eq(UserItem::getUserId, userId)
                .eq(UserItem::getItemId, itemId));

        if (userItem == null) {
            userItem = new UserItem();
            userItem.setUserId(userId);
            userItem.setItemId(itemId);
            userItem.setQuantity(quantity);
            userItem.setIsEquipped(false);
            userItemMapper.insert(userItem);
        } else {
            userItem.setQuantity(userItem.getQuantity() + quantity);
            userItemMapper.updateById(userItem);
        }

        return ApiResponse.success("购买成功，获得" + quantity + "个" + item.getName());
    }

    @Transactional
    public ApiResponse<String> equipItem(Long userId, Long itemId) {
        UserItem userItem = userItemMapper.selectOne(new LambdaQueryWrapper<UserItem>()
                .eq(UserItem::getUserId, userId)
                .eq(UserItem::getItemId, itemId));

        if (userItem == null || userItem.getQuantity() <= 0) {
            return ApiResponse.error("没有该道具");
        }

        Item item = itemMapper.selectById(itemId);
        
        // 先卸下同类装备
        List<UserItem> equippedItems = userItemMapper.selectList(
                new LambdaQueryWrapper<UserItem>()
                        .eq(UserItem::getUserId, userId)
                        .eq(UserItem::getIsEquipped, true));
        
        for (UserItem equipped : equippedItems) {
            Item equippedItem = itemMapper.selectById(equipped.getItemId());
            if (equippedItem != null && equippedItem.getCategory().equals(item.getCategory())) {
                equipped.setIsEquipped(false);
                userItemMapper.updateById(equipped);
            }
        }

        // 穿上新装备
        userItem.setIsEquipped(true);
        userItemMapper.updateById(userItem);

        return ApiResponse.success("穿戴成功");
    }

    @Transactional
    public ApiResponse<String> useItem(Long userId, Long itemId) {
        UserItem userItem = userItemMapper.selectOne(new LambdaQueryWrapper<UserItem>()
                .eq(UserItem::getUserId, userId)
                .eq(UserItem::getItemId, itemId));

        if (userItem == null || userItem.getQuantity() <= 0) {
            return ApiResponse.error("没有该道具");
        }

        Item item = itemMapper.selectById(itemId);
        
        // 消耗道具
        if (userItem.getQuantity() == 1) {
            userItemMapper.deleteById(userItem);
        } else {
            userItem.setQuantity(userItem.getQuantity() - 1);
            userItemMapper.updateById(userItem);
        }

        // 应用道具效果（这里简化处理，实际应更新宠物属性）
        String effect = item.getEffect();
        // 效果已在道具表中，可在宠物Service中应用

        return ApiResponse.success("使用成功");
    }

    public List<UserItem> getUserItems(Long userId) {
        return userItemMapper.selectList(new LambdaQueryWrapper<UserItem>()
                .eq(UserItem::getUserId, userId)
                .orderByDesc(UserItem::getCreateTime));
    }

    public Map<String, Object> getUserBackpack(Long userId) {
        List<UserItem> userItems = getUserItems(userId);
        Map<String, Object> result = new HashMap<>();
        
        List<Map<String, Object>> items = new java.util.ArrayList<>();
        for (UserItem ui : userItems) {
            Item item = itemMapper.selectById(ui.getItemId());
            if (item != null) {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("itemId", item.getId());
                itemMap.put("name", item.getName());
                itemMap.put("category", item.getCategory());
                itemMap.put("icon", item.getIcon());
                itemMap.put("quantity", ui.getQuantity());
                itemMap.put("isEquipped", ui.getIsEquipped());
                items.add(itemMap);
            }
        }
        
        result.put("items", items);
        User user = userMapper.selectById(userId);
        result.put("gold", user != null ? user.getGold() : 0);
        
        return result;
    }
}
