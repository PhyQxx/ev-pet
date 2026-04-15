package com.evpet.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.evpet.mapper.PetMapper;
import com.evpet.mapper.UserMapper;
import com.evpet.mapper.WorkRecordMapper;
import com.evpet.model.Pet;
import com.evpet.model.User;
import com.evpet.model.WorkRecord;
import com.evpet.vo.ApiResponse;
import com.evpet.vo.WorkVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkService {

    private final WorkRecordMapper workRecordMapper;
    private final UserMapper userMapper;
    private final PetMapper petMapper;

    // 打工配置
    private static final List<WorkVO.WorkItemVO> WORK_TEMPLATES = List.of(
        WorkVO.WorkItemVO.builder().id(1L).name("派发传单").description("在公园帮助商家派发传单").icon("📄").duration(30).earnings(50).exp(10).status("available").build(),
        WorkVO.WorkItemVO.builder().id(2L).name("外卖配送").description("帮助送外卖赚取报酬").icon("🛵").duration(45).earnings(80).exp(15).status("available").build(),
        WorkVO.WorkItemVO.builder().id(3L).name("餐厅帮厨").description("在餐厅帮忙准备食材").icon("🍳").duration(60).earnings(120).exp(20).status("available").build(),
        WorkVO.WorkItemVO.builder().id(4L).name("家教辅导").description("帮助小朋友辅导作业").icon("📚").duration(90).earnings(200).exp(30).status("available").build(),
        WorkVO.WorkItemVO.builder().id(5L).name("宠物美容").description("帮助其他宠物做美容服务").icon("✨").duration(40).earnings(100).exp(18).status("available").build()
    );

    public WorkVO getWorkInfo(Long userId) {
        User user = userMapper.selectById(userId);
        Pet pet = petMapper.selectOne(new LambdaQueryWrapper<Pet>().eq(Pet::getUserId, userId));
        
        // 获取进行中的打工
        List<WorkVO.WorkItemVO> works = new ArrayList<>(WORK_TEMPLATES);
        List<WorkRecord> ongoing = workRecordMapper.selectList(
            new LambdaQueryWrapper<WorkRecord>()
                .eq(WorkRecord::getUserId, userId)
                .eq(WorkRecord::getStatus, "ongoing")
        );
        
        for (WorkVO.WorkItemVO work : works) {
            for (WorkRecord record : ongoing) {
                if (work.getId().equals(record.getId())) {
                    work.setStatus("ongoing");
                    long remaining = java.time.Duration.between(LocalDateTime.now(), record.getEndTime()).getSeconds();
                    work.setRemainingTime(Math.max(0, remaining));
                }
            }
        }
        
        // 获取打工记录
        List<WorkRecord> records = workRecordMapper.selectList(
            new LambdaQueryWrapper<WorkRecord>()
                .eq(WorkRecord::getUserId, userId)
                .eq(WorkRecord::getStatus, "completed")
                .orderByDesc(WorkRecord::getCreateTime)
                .last("LIMIT 10")
        );
        
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        List<WorkVO.WorkRecordVO> recordVOs = records.stream().map(r -> 
            WorkVO.WorkRecordVO.builder()
                .id(r.getId())
                .name(r.getWorkName())
                .earnings(r.getEarnings())
                .exp(r.getExp())
                .startTime(r.getStartTime().format(fmt))
                .endTime(r.getEndTime().format(fmt))
                .build()
        ).toList();
        
        // 统计
        LocalDateTime today = LocalDateTime.now().toLocalDate().atStartOfDay();
        List<WorkRecord> todayRecords = workRecordMapper.selectList(
            new LambdaQueryWrapper<WorkRecord>()
                .eq(WorkRecord::getUserId, userId)
                .eq(WorkRecord::getStatus, "completed")
                .ge(WorkRecord::getCreateTime, today)
        );
        
        int todayEarnings = todayRecords.stream().mapToInt(WorkRecord::getEarnings).sum();
        List<WorkRecord> allRecords = workRecordMapper.selectList(
            new LambdaQueryWrapper<WorkRecord>()
                .eq(WorkRecord::getUserId, userId)
                .eq(WorkRecord::getStatus, "completed")
        );
        
        WorkVO.WorkStatsVO stats = WorkVO.WorkStatsVO.builder()
            .todayEarnings(todayEarnings)
            .totalEarnings(allRecords.stream().mapToInt(WorkRecord::getEarnings).sum())
            .totalWorkCount(allRecords.size())
            .build();
        
        return WorkVO.builder()
            .availableWorks(works)
            .recentRecords(recordVOs)
            .stats(stats)
            .build();
    }

    @Transactional
    public ApiResponse<String> startWork(Long userId, Long workId) {
        WorkVO.WorkItemVO workTemplate = WORK_TEMPLATES.stream()
            .filter(w -> w.getId().equals(workId))
            .findFirst()
            .orElse(null);
        
        if (workTemplate == null) {
            return ApiResponse.error("打工任务不存在");
        }
        
        Pet pet = petMapper.selectOne(new LambdaQueryWrapper<Pet>().eq(Pet::getUserId, userId));
        if (pet == null) {
            return ApiResponse.error("宠物不存在");
        }
        
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusMinutes(workTemplate.getDuration());
        
        WorkRecord record = new WorkRecord();
        record.setUserId(userId);
        record.setPetId(pet.getId());
        record.setWorkName(workTemplate.getName());
        record.setDuration(workTemplate.getDuration() * 60);
        record.setEarnings(workTemplate.getEarnings());
        record.setExp(workTemplate.getExp());
        record.setStartTime(startTime);
        record.setEndTime(endTime);
        record.setStatus("ongoing");
        record.setCreateTime(LocalDateTime.now());
        workRecordMapper.insert(record);
        
        return ApiResponse.success("开始打工：" + workTemplate.getName());
    }

    @Transactional
    public ApiResponse<WorkVO.WorkStatsVO> claimWorkReward(Long userId, Long recordId) {
        WorkRecord record = workRecordMapper.selectOne(
            new LambdaQueryWrapper<WorkRecord>()
                .eq(WorkRecord::getId, recordId)
                .eq(WorkRecord::getUserId, userId)
                .eq(WorkRecord::getStatus, "ongoing")
        );
        
        if (record == null) {
            return ApiResponse.error("打工记录不存在或已完成");
        }
        
        // 检查是否到时间
        if (LocalDateTime.now().isBefore(record.getEndTime())) {
            return ApiResponse.error("打工尚未完成");
        }
        
        // 标记完成
        record.setStatus("completed");
        workRecordMapper.updateById(record);
        
        // 给用户加金币和经验
        User user = userMapper.selectById(userId);
        user.setGold(user.getGold() + record.getEarnings());
        user.setExp(user.getExp() + record.getExp());
        userMapper.updateById(user);
        
        // 给宠物加经验
        Pet pet = petMapper.selectById(record.getPetId());
        pet.setExp(pet.getExp() + record.getExp());
        pet.setUpdateTime(LocalDateTime.now());
        petMapper.updateById(pet);
        
        WorkVO workInfo = getWorkInfo(userId);
        return ApiResponse.<WorkVO.WorkStatsVO>builder()
            .code(200)
            .message("领取成功，获得" + record.getEarnings() + "金币")
            .data(workInfo.getStats())
            .build();
    }
}
