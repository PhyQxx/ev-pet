package com.evpet.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.evpet.mapper.PetMapper;
import com.evpet.mapper.UserMapper;
import com.evpet.model.Pet;
import com.evpet.model.User;
import com.evpet.utils.PetUtil;
import com.evpet.vo.PetVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetMapper petMapper;
    private final UserMapper userMapper;
    private final AchievementService achievementService;

    public PetVO getPetInfo(Long userId) {
        Pet pet = petMapper.selectOne(new LambdaQueryWrapper<Pet>()
                .eq(Pet::getUserId, userId));
        if (pet == null) {
            return null;
        }
        // 更新属性（根据时间衰减）
        updatePetAttributes(pet);
        return toPetVO(pet);
    }

    @Transactional
    public PetVO feed(Long userId) {
        Pet pet = getPetByUserId(userId);
        pet.setFullness(Math.min(100, pet.getFullness() + 20));
        pet.setExp(pet.getExp() + 5);
        pet.setLastFeedTime(LocalDateTime.now());
        checkEvolution(pet);
        petMapper.updateById(pet);
        updateUserExp(userId, 5);
        // 触发成就检查（喂食次数）
        achievementService.checkAndGrantAchievement(userId, 1, pet.getExp() / 10);
        return toPetVO(pet);
    }

    @Transactional
    public PetVO bath(Long userId) {
        Pet pet = getPetByUserId(userId);
        pet.setHealth(Math.min(100, pet.getHealth() + 20));
        pet.setExp(pet.getExp() + 5);
        pet.setLastBathTime(LocalDateTime.now());
        checkEvolution(pet);
        petMapper.updateById(pet);
        updateUserExp(userId, 5);
        return toPetVO(pet);
    }

    @Transactional
    public PetVO play(Long userId) {
        Pet pet = getPetByUserId(userId);
        pet.setMood(Math.min(100, pet.getMood() + 20));
        pet.setHealth(Math.max(0, pet.getHealth() - 5)); // 玩累了消耗体力
        pet.setExp(pet.getExp() + 10);
        pet.setLastPlayTime(LocalDateTime.now());
        checkEvolution(pet);
        petMapper.updateById(pet);
        updateUserExp(userId, 10);
        // 触发成就检查（陪玩次数）
        achievementService.checkAndGrantAchievement(userId, 2, pet.getExp() / 20);
        return toPetVO(pet);
    }

    @Transactional
    public PetVO updateName(Long userId, String name) {
        Pet pet = getPetByUserId(userId);
        pet.setName(name);
        pet.setUpdateTime(LocalDateTime.now());
        petMapper.updateById(pet);
        return toPetVO(pet);
    }

    private void updatePetAttributes(Pet pet) {
        LocalDateTime now = LocalDateTime.now();
        
        // 每小时饱食度-5
        long hoursSinceFeed = ChronoUnit.HOURS.between(pet.getLastFeedTime(), now);
        int fullnessDecay = (int) (hoursSinceFeed * 5);
        pet.setFullness(Math.max(0, pet.getFullness() - fullnessDecay));

        // 每小时体力-3
        long hoursSinceBath = ChronoUnit.HOURS.between(pet.getLastBathTime(), now);
        int healthDecay = (int) (hoursSinceBath * 3);
        pet.setHealth(Math.max(0, pet.getHealth() - healthDecay));

        // 每小时心情-2
        long hoursSincePlay = ChronoUnit.HOURS.between(pet.getLastPlayTime(), now);
        int moodDecay = (int) (hoursSincePlay * 2);
        pet.setMood(Math.max(0, pet.getMood() - moodDecay));

        pet.setUpdateTime(now);
        // 更新时间戳防止下次重复衰减
        if (fullnessDecay > 0) pet.setLastFeedTime(now);
        if (healthDecay > 0) pet.setLastBathTime(now);
        if (moodDecay > 0) pet.setLastPlayTime(now);
        petMapper.updateById(pet);
    }

    private void checkEvolution(Pet pet) {
        int exp = pet.getExp();
        int stage = pet.getStage();
        int level = pet.getLevel();

        // 进化检查
        if (stage == 1 && exp >= 100) {
            pet.setStage(2);
            pet.setLevel(pet.getLevel() + 1);
        } else if (stage == 2 && exp >= 500) {
            pet.setStage(3);
            pet.setLevel(pet.getLevel() + 1);
        }

        // 升级检查
        int expForLevel = level * 50;
        if (exp >= expForLevel) {
            pet.setLevel(level + 1);
        }
    }

    private void updateUserExp(Long userId, int exp) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setExp(user.getExp() + exp);
            int levelExp = user.getLevel() * 100;
            if (user.getExp() >= levelExp) {
                user.setLevel(user.getLevel() + 1);
            }
            userMapper.updateById(user);
        }
    }

    private Pet getPetByUserId(Long userId) {
        return petMapper.selectOne(new LambdaQueryWrapper<Pet>()
                .eq(Pet::getUserId, userId));
    }

    private PetVO toPetVO(Pet pet) {
        return PetUtil.toPetVO(pet);
    }

    private String calculateStatus(Pet pet) {
        return PetUtil.calculateStatus(pet);
    }

    private Long calculateNextEvolutionExp(int stage) {
        return PetUtil.calculateNextEvolutionExp(stage);
    }
}
