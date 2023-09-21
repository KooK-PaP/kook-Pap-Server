package com.KooKPaP.server.global.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // redis(Lettuce)와 연결시키는 빈
        return new LettuceConnectionFactory(host, port);
    }

    @Bean
    public RedisTemplate<String,Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        // Key는 String으로 (역)직렬화하도록 설정
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // Java 객체를 JSON 형식으로 직렬화, 역직렬화하는 Serializer
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);

        ObjectMapper om = new ObjectMapper();
        // PropertyAccessor: 객체의 모든 필드에 대해서, JsonAutoDetect.Visibility.ANY: 모든 종류의 접근자(public, private, protected)에 대해 직렬, 역직렬화 가능하게 설정
        // 원래 Jackson은 공개된 필드나 getter setter가 설정된 필드만 포함시킴
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        // activateDefaultTyping: JSON에 실제 타입 정보를 포함시킴
        // LaissezFaireSubTypeValidator.instance: 서브타입 검사기로, 기본적으로 안전한 서브타입만 역직렬화를 허용하는 역할
        // ObjectMapper.DefaultTyping.NON_FINAL:  non-final 클래스들에 대해서만 유형 정보를 포함시키라고 지시. -> final 클래스는 유형 정보 없이도 역직렬화가 가능해짐.
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);

        jackson2JsonRedisSerializer.setObjectMapper(om);
        // 위에서 설정한 Jackson 파싱 모듈로 value를 (역)직렬화 하도록 설정
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);

        return redisTemplate;
    }
}
