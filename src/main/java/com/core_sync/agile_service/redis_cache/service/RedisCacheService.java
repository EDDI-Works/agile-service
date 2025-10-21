package com.core_sync.agile_service.redis_cache.service;

import java.time.Duration;

public interface RedisCacheService {
    <K, V> void setKeyAndValue(K key, V value);
    <K, V> void setKeyAndValue(K key, V value, Duration ttl);
    <T> T getValueByKey(String key, Class<T> clazz);
    void deleteByKey(String key);
}
