package com.KooKPaP.server.global.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate redisTemplate;

    public void setValue(String key, Object value, long timeout, TimeUnit unit) {
        // <key,value>를 TTL 둬서 redis에 저장
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, value, timeout, unit);
    }

    public Object getValue(String key) {
        // key값으로 value 가져오기
        // 존재하지 않으면, null 값 반환
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        Object object = valueOperations.get(key);

        return object;
    }

    public void deleteValue(String key) {
        // key값으로 value 삭제하기
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.getAndDelete(key);
    }
}
