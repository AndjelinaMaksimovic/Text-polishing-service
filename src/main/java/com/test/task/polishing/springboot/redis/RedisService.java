package com.test.task.polishing.springboot.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public synchronized void setCache(String key, String[] value) {
        redisTemplate.opsForList().getOperations().delete(key);

        for (String v : value) {
            redisTemplate.opsForList().leftPush(key, v);
        }

    }

    public boolean findValueInRedis(String key, String value) {
        List<String> list = redisTemplate.opsForList().range(key, 0, -1);

        if (list != null) {
            return list.contains(value);
        }

        return false;
    }

}