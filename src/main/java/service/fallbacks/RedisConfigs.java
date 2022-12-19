package service.fallbacks;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RedisConfigs {
    private int ttl;
    private int timeout;
    private int maxAttempts;
}
