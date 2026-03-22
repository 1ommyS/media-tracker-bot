package com.indistudia.cache;

public record CacheEntry(Object value, long expiresAtMillis) {
    public boolean isExpired() {
        return System.currentTimeMillis() >= expiresAtMillis;
    }
}
