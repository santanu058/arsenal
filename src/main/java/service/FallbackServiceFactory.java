package service;

import com.google.inject.Inject;
import service.redis.RedisFallbackService;

public class FallbackServiceFactory<V> {

    @Inject
    RedisFallbackService<V> redisFallbackService;

    public FallbackService getFallbackType(FallbackType fallbackType) {
        switch (fallbackType) {
            case REDIS: return redisFallbackService;
            default: throw new RuntimeException("Not Implemented Yet");
        }
    }
}
