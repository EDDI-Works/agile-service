package com.core_sync.agile_service.redis_cache.service;

import com.core_sync.agile_service.redis_cache.type.RedisTypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCacheServiceImpl implements RedisCacheService {

    final private StringRedisTemplate redisTemplate;

    @Override
    public <K, V> void setKeyAndValue(K key, V value) {
        setKeyAndValue(key, value, Duration.ofMinutes(720));  // 기본 TTL 720분 적용
    }

    @Override
    public <K, V> void setKeyAndValue(K key, V value, Duration ttl) {
        String keyAsString = String.valueOf(key);
        String valueAsString = String.valueOf(value);

        try {
            ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
            valueOps.set(keyAsString, valueAsString, ttl);
            log.debug("캐시에 데이터 저장 성공 - key: {}, ttl: {}", keyAsString, ttl);
        } catch (Exception e) {
            log.error("Redis 캐시에 데이터 저장 실패 - key: {}, error: {}", keyAsString, e.getMessage(), e);
            throw new RuntimeException("캐시에 데이터를 저장할 수 없습니다.", e);
        }
    }

    @Override
    public <T> T getValueByKey(String key, Class<T> clazz) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String value;

        try {
            value = ops.get(key);
        } catch (Exception e) {
            log.error("Redis 조회 실패: key={}, error={}", key, e.getMessage(), e);
            throw new RuntimeException("Redis 조회 중 오류가 발생했습니다.", e);
        }

        if (value == null) {
            return null;
        }

        Function<String, ?> converter = RedisTypeConverter.getConverter(clazz);
        if (converter == null) {
            throw new IllegalArgumentException("지원하지 않는 클래스 타입: " + clazz);
        }

        try {
            return clazz.cast(converter.apply(value));
        } catch (Exception e) {
            log.error("값 변환 실패: key={}, value={}, targetClass={}", key, value, clazz.getSimpleName(), e);
            throw new IllegalArgumentException("값을 " + clazz.getSimpleName() + "로 변환할 수 없습니다.", e);
        }
    }

    @Override
    public void deleteByKey(String key) {
        redisTemplate.delete(key);
    }
}
