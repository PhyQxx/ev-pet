package com.evpet.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.evpet.mapper.SystemConfigMapper;
import com.evpet.model.SystemConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SystemConfigService {

    private final SystemConfigMapper systemConfigMapper;
    private final StringRedisTemplate redisTemplate;

    private static final String CACHE_PREFIX = "system_config:";
    private static final long CACHE_TTL_HOURS = 24;

    public String getByKey(String key) {
        // 先查 Redis 缓存
        String cached = redisTemplate.opsForValue().get(CACHE_PREFIX + key);
        if (cached != null) {
            return cached;
        }

        // 查数据库
        SystemConfig config = systemConfigMapper.selectOne(
                new LambdaQueryWrapper<SystemConfig>().eq(SystemConfig::getConfigKey, key)
        );
        if (config == null) {
            return null;
        }

        // 写入缓存
        redisTemplate.opsForValue().set(CACHE_PREFIX + key, config.getConfigValue());
        return config.getConfigValue();
    }

    public String getByKeyOrDefault(String key, String defaultValue) {
        String value = getByKey(key);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }

    public List<SystemConfig> getAll() {
        return systemConfigMapper.selectList(null);
    }

    public Map<String, String> getAllAsMap() {
        return getAll().stream()
                .collect(Collectors.toMap(
                        SystemConfig::getConfigKey,
                        c -> c.getConfigValue() != null ? c.getConfigValue() : "",
                        (a, b) -> a
                ));
    }

    public void setByKey(String key, String value) {
        SystemConfig config = systemConfigMapper.selectOne(
                new LambdaQueryWrapper<SystemConfig>().eq(SystemConfig::getConfigKey, key)
        );
        if (config != null) {
            config.setConfigValue(value);
            config.setUpdateTime(LocalDateTime.now());
            systemConfigMapper.updateById(config);
        } else {
            config = new SystemConfig();
            config.setConfigKey(key);
            config.setConfigValue(value);
            systemConfigMapper.insert(config);
        }
        // 更新缓存
        redisTemplate.opsForValue().set(CACHE_PREFIX + key, value);
    }

    public void batchSave(Map<String, String> configMap) {
        configMap.forEach(this::setByKey);
    }

    public void clearCache() {
        var keys = redisTemplate.keys(CACHE_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
