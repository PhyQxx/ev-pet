package com.evpet.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.evpet.dto.LoginDTO;
import com.evpet.mapper.PetMapper;
import com.evpet.mapper.UserMapper;
import com.evpet.model.Pet;
import com.evpet.model.User;
import com.evpet.utils.JwtUtil;
import com.evpet.utils.PetUtil;
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
    public LoginVO login(LoginDTO dto, String clientIp) {
        User user = null;

        if (dto.getLoginType() == 1) {
            // 微信登录
            String openId = dto.getCode();
            // 非真实微信code（前端生成的带前缀标识）则用IP标识
            if (openId == null || openId.startsWith("web_") || openId.startsWith("uni_")) {
                openId = "ip_" + clientIp;
            }
            final String finalOpenId = openId;
            user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                    .eq(User::getOpenId, finalOpenId));
            if (user == null) {
                user = createUserByWechat(finalOpenId);
            }
        } else if (dto.getLoginType() == 2) {
            // 游客/手机号登录 - 用IP标识
            String identifier = "ip_" + clientIp;
            user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                    .eq(User::getPhone, identifier));
            if (user == null) {
                user = createUserByPhone(identifier);
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

    @Transactional
    public void deleteAccount(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        // Deactivate account (soft delete)
        user.setStatus(0);
        user.setNickname("已注销用户");
        user.setAvatar(null);
        userMapper.updateById(user);
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
