package com.evpet.dto;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WorkDTO {
    @NotNull(message = "打工类型不能为空")
    private Long workId;
    private Integer quantity;
}
