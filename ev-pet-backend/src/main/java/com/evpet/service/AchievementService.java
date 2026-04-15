package com.evpet.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.evpet.mapper.*;
import com.evpet.model.*;
import com.evpet.vo.ApiResponse;
import com.evpet.vo.AchievementVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementMapper achievementMapper;
    private final UserAchievementMapper userAchievementMapper;
    private final UserMapper userMapper;
    private final PetMapper petMapper;

    public AchievementVO getAchievements(Long userId) {
        User user = userMapper.selectById(userId);
        Pet pet = petMapper.selectOne(new LambdaQueryWrapper<Pet>().eq(Pet::getUserId, userId));
        
        List<Achievement> allAchievements = achievementMapper.selectList(null);
        List<UserAchievement> userAchievements = userAchievementMapper.selectList(
            new LambdaQueryWrapper<UserAchievement>().eq(UserAchievement::getUserId, userId)
        );
        
        Map<Long, UserAchievement> userAchMap = userAchievements.stream()
            .collect(Collectors.toMap(UserAchievement::getAchievementId, ua -> ua));
        
        List<AchievementVO.AchievementItemVO> items = new ArrayList<>();
        
        for (Achievement ach : allAchievements) {
            UserAchievement ua = userAchMap.get(ach.getId());
            boolean completed = ua != null;
            boolean claimed = ua != null && ua.getIsClaimed();
            
            int currentProgress = calculateProgress(user, pet, ach);
            int progress = ach.getConditionType() == 4 ? (completed ? 1 : 0) : currentProgress;
            
            items.add(AchievementVO.AchievementItemVO.builder()
                .id(ach.getId())
                .name(ach.getName())
                .description(ach.getDescription())
                .icon(getAchievementIcon(ach.getIcon()))
                .rewardGold(ach.getRewardGold())
                .conditionType(ach.getConditionType())
                .conditionValue(ach.getConditionValue())
                .currentProgress(progress)
                .completed(completed)
                .claimed(claimed)
                .build());
        }
        
        int completedCount = (int) items.stream().filter(AchievementVO.AchievementItemVO::getCompleted).count();
        int totalReward = items.stream()
            .filter(AchievementVO.AchievementItemVO::getCompleted)
            .mapToInt(AchievementVO.AchievementItemVO::getRewardGold)
            .sum();
        
        return AchievementVO.builder()
            .achievements(items)
            .completedCount(completedCount)
            .totalCount(allAchievements.size())
            .totalReward(totalReward)
            .build();
    }

    @Transactional
    public ApiResponse<String> claimReward(Long userId, Long achievementId) {
        UserAchievement ua = userAchievementMapper.selectOne(
            new LambdaQueryWrapper<UserAchievement>()
                .eq(UserAchievement::getUserId, userId)
                .eq(UserAchievement::getAchievementId, achievementId)
        );
        
        if (ua == null) {
            return ApiResponse.error("成就未完成，无法领取");
        }
        
        if (ua.getIsClaimed()) {
            return ApiResponse.error("奖励已领取");
        }
        
        Achievement achievement = achievementMapper.selectById(achievementId);
        
        // 领取奖励
        ua.setIsClaimed(true);
        ua.setClaimTime(java.time.LocalDateTime.now());
        userAchievementMapper.updateById(ua);
        
        // 给用户加金币
        User user = userMapper.selectById(userId);
        user.setGold(user.getGold() + achievement.getRewardGold());
        userMapper.updateById(user);
        
        return ApiResponse.success("领取成功，获得" + achievement.getRewardGold() + "金币");
    }

    @Transactional
    public void checkAndGrantAchievement(Long userId, int conditionType, int conditionValue) {
        List<Achievement> achievements = achievementMapper.selectList(
            new LambdaQueryWrapper<Achievement>()
                .eq(Achievement::getConditionType, conditionType)
                .eq(Achievement::getConditionValue, conditionValue)
        );
        
        User user = userMapper.selectById(userId);
        Pet pet = petMapper.selectOne(new LambdaQueryWrapper<Pet>().eq(Pet::getUserId, userId));
        
        for (Achievement ach : achievements) {
            UserAchievement existing = userAchievementMapper.selectOne(
                new LambdaQueryWrapper<UserAchievement>()
                    .eq(UserAchievement::getUserId, userId)
                    .eq(UserAchievement::getAchievementId, ach.getId())
            );
            
            if (existing == null) {
                UserAchievement ua = new UserAchievement();
                ua.setUserId(userId);
                ua.setAchievementId(ach.getId());
                ua.setIsClaimed(false);
                ua.setCreateTime(java.time.LocalDateTime.now());
                userAchievementMapper.insert(ua);
            }
        }
    }

    private int calculateProgress(User user, Pet pet, Achievement ach) {
        if (pet == null) return 0;
        
        return switch (ach.getConditionType()) {
            case 1 -> pet.getExp() / 10; // 喂食次数(简化)
            case 2 -> pet.getExp() / 20; // 陪玩次数(简化)
            case 3 -> pet.getLevel();
            case 4 -> pet.getStage();
            default -> 0;
        };
    }

    private String getAchievementIcon(String icon) {
        return switch (icon) {
            case "egg" -> "🥚";
            case "food" -> "🍖";
            case "bath" -> "🛁";
            case "play" -> "🎮";
            case "level" -> "⭐";
            case "evolution" -> "✨";
            default -> "🏆";
        };
    }
}
