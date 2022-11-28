package service;

import service.redis.RedisFallbackService;

public abstract class FallbackService<V> {
    public abstract V get(String key);

}
