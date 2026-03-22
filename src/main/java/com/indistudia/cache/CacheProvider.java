package com.indistudia.cache;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.*;

@Slf4j
public class CacheProvider {
    private static final ConcurrentHashMap<String, CacheEntry> map = new ConcurrentHashMap<String, CacheEntry>();

    private static long defaultTtlMillis = Duration.ofMinutes(5).toMillis();
    private static final long cleanUpPeriodMillis = Duration.ofSeconds(30).toMillis();

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void startCleanUpTask() {
        scheduler.scheduleWithFixedDelay(CacheProvider::cleanUpExpiresEntries, cleanUpPeriodMillis, cleanUpPeriodMillis, TimeUnit.MILLISECONDS);
    }

    private static void cleanUpExpiresEntries() {
        for (var entry : map.entrySet()) {
            if (entry.getValue().isExpired()) {
                map.remove(entry.getKey());
            }
        }
    }

    public static void set(String key, Object value) {
        var cacheEntry = new CacheEntry(value, System.currentTimeMillis() + defaultTtlMillis);
        map.put(key, cacheEntry);
    }

    public static void set(String key, Object value, long ttlMillis) {
        var cacheEntry = new CacheEntry(value, System.currentTimeMillis() + ttlMillis);
        map.put(key, cacheEntry);
    }

    public static <T> T get(String key) {
        log.atInfo().addKeyValue("cacheKey", key).log("Reading value from redis");

        CacheEntry cacheEntry = map.get(key);

        if (cacheEntry == null) {
            return null;
        }

        if (cacheEntry.isExpired()) {
            return null;
        }

        return (T) cacheEntry.value();
    }
}
