package com.indistudia.cache;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class CacheProvider {
    private static final ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<String, Object>();

    public static void set(String key, Object value) {
        map.put(key, value);
    }

    public static <T> T get(String key) {
        log.atInfo().addKeyValue("cacheKey", key).log("Reading value from redis");
        return (T) map.get(key);
    }
}
