package com.core_sync.agile_service.redis_cache.type;

import java.util.function.Function;

public enum RedisTypeConverter {
    STRING(String.class, Function.identity()),
    LONG(Long.class, Long::valueOf),
    INTEGER(Integer.class, Integer::valueOf),
    BOOLEAN(Boolean.class, Boolean::valueOf);

    private final Class<?> type;
    private final Function<String, ?> converter;

    RedisTypeConverter(Class<?> type, Function<String, ?> converter) {
        this.type = type;
        this.converter = converter;
    }

    public static <T> Function<String, ?> getConverter(Class<T> clazz) {
        for (RedisTypeConverter rtc : values()) {
            if (rtc.type.equals(clazz)) return rtc.converter;
        }
        return null;
    }
}

