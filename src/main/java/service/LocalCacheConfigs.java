package service;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LocalCacheConfigs {
    private int cacheSize;
    private int expiryTime;
    private int ttl = 5; // TimeUnit.MINUTES
}
