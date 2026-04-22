package com.evpet.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("items")
public class Item {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;             // 道具名
    private String description;      // 描述
    private String category;         // 分类 food/clothing/accessory/bg
    private Integer price;           // 价格(金币)
    private BigDecimal rmbPrice;     // 价格(RMB)
    private String icon;             // 图标URL
    private String effect;           // 效果 JSON
    private Boolean isFree;         // 是否免费
    private Integer status;         // 1=上架, 0=下架
    private LocalDateTime createTime;
}
