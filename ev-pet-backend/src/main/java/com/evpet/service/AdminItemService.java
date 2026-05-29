package com.evpet.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.evpet.mapper.ItemMapper;
import com.evpet.model.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminItemService {

    private final ItemMapper itemMapper;

    public Map<String, Object> getItems(int page, int pageSize, String category, Integer status) {
        Page<Item> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<Item> wrapper = new LambdaQueryWrapper<>();
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Item::getCategory, category);
        }
        if (status != null) {
            wrapper.eq(Item::getStatus, status);
        }
        wrapper.orderByDesc(Item::getCreateTime);
        Page<Item> result = itemMapper.selectPage(p, wrapper);

        Map<String, Object> resp = new HashMap<>();
        resp.put("list", result.getRecords());
        resp.put("total", result.getTotal());
        resp.put("page", result.getCurrent());
        resp.put("pageSize", result.getSize());
        return resp;
    }

    public void createItem(Item item) {
        item.setCreateTime(LocalDateTime.now());
        itemMapper.insert(item);
    }

    public void updateItem(Long id, Item data) {
        Item item = itemMapper.selectById(id);
        if (item == null) throw new IllegalArgumentException("道具不存在");
        if (data.getName() != null) item.setName(data.getName());
        if (data.getDescription() != null) item.setDescription(data.getDescription());
        if (data.getCategory() != null) item.setCategory(data.getCategory());
        if (data.getPrice() != null) item.setPrice(data.getPrice());
        if (data.getRmbPrice() != null) item.setRmbPrice(data.getRmbPrice());
        if (data.getIcon() != null) item.setIcon(data.getIcon());
        if (data.getEffect() != null) item.setEffect(data.getEffect());
        if (data.getIsFree() != null) item.setIsFree(data.getIsFree());
        itemMapper.updateById(item);
    }

    public void deleteItem(Long id) {
        itemMapper.deleteById(id);
    }

    public String toggleItemStatus(Long id) {
        Item item = itemMapper.selectById(id);
        if (item == null) throw new IllegalArgumentException("道具不存在");
        item.setStatus(item.getStatus() == 1 ? 0 : 1);
        itemMapper.updateById(item);
        return item.getStatus() == 1 ? "上架成功" : "下架成功";
    }
}
