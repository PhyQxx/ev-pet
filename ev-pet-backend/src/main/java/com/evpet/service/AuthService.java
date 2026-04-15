package com.evpet.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.evpet.dto.LoginDTO;
import com.evpet.mapper.PetMapper;
import com.evpet.mapper.UserMapper;
import com.evpet.model.Pet;
import com.evpet.model.User;
import com.evpet.utils.JwtUtil;
import com.evpet.vo.LoginVO;
import com.evpet.vo.PetVO;
import com.evpet.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final PetMapper petMapper;
    private final JwtUtil jwtUtil;

    @Transactional
    public LoginVO login(LoginDTO dto) {
        User user = null;

        if (dto.getLoginType() == 1) {
            // 微信登录 - 简化版，实际应调微信API
            user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                    .eq(User::getOpenId, dto.getCode()));
            if (user == null) {
                user = createUserByWechat(dto.getCode());
            }
        } else if (dto.getLoginType() == 2) {
            // 手机号登录
            user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                    .eq(User::getPhone, dto.getPhone()));
            if (user == null) {
                user = createUserByPhone(dto.getPhone());
            }
        }

        // 获取或创建宠物
        Pet pet = petMapper.selectOne(new LambdaQueryWrapper<Pet>()
                .eq(Pet::getUserId, user.getId()));
        if (pet == null) {
            pet = createPet(user.getId());
        }

        String token = jwtUtil.generateToken(user.getId());

        return LoginVO.builder()
                .token(token)
                .user(toUserVO(user))
                .pet(toPetVO(pet))
                .build();
    }

    private User createUserByWechat(String openId) {
        User user = new User();
        user.setOpenId(openId);
        user.setNickname("用户" + System.currentTimeMillis() % 10000);
        user.setGold(1000);
        user.setLevel(1);
        user.setExp(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insert(user);
        return user;
    }

    private User createUserByPhone(String phone) {
        User user = new User();
        user.setPhone(phone);
        user.setNickname("用户" + System.currentTimeMillis() % 10000);
        user.setGold(1000);
        user.setLevel(1);
        user.setExp(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insert(user);
        return user;
    }

    private Pet createPet(Long userId) {
        Pet pet = new Pet();
        pet.setUserId(userId);
        pet.setName("小可爱");
        pet.setStage(1);
        pet.setLevel(1);
        pet.setExp(0);
        pet.setHealth(100);
        pet.setFullness(100);
        pet.setMood(100);
        pet.setLastFeedTime(LocalDateTime.now());
        pet.setLastBathTime(LocalDateTime.now());
        pet.setLastPlayTime(LocalDateTime.now());
        pet.setCreateTime(LocalDateTime.now());
        pet.setUpdateTime(LocalDateTime.now());
        petMapper.insert(pet);
        return pet;
    }

    private UserVO toUserVO(User user) {
        return UserVO.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .gold(user.getGold())
                .level(user.getLevel())
                .exp(user.getExp())
                .build();
    }

    private PetVO toPetVO(Pet pet) {
        return PetVO.builder()
                .id(pet.getId())
                .name(pet.getName())
                .stage(pet.getStage())
                .level(pet.getLevel())
                .exp(pet.getExp())
                .health(pet.getHealth())
                .fullness(pet.getFullness())
                .mood(pet.getMood())
                .status(calculateStatus(pet))
                .nextEvolutionExp(calculateNextEvolutionExp(pet.getStage()))
                .build();
    }

    private String calculateStatus(Pet pet) {
        if (pet.getMood() < 30) return "angry";
        if (pet.getFullness() < 30) return "hungry";
        if (pet.getHealth() < 30) return "tired";
        return "happy";
    }

    private Long calculateNextEvolutionExp(int stage) {
        return switch (stage) {
            case 1 -> 100L;  // 100 exp to stage 2
            case 2 -> 500L;  // 500 exp to stage 3
            default -> 0L;
        };
    }
}
