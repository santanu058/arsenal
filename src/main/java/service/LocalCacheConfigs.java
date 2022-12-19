package service;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class LocalCacheConfigs {
    private int cacheSize;
    private int ttl; // TimeUnit.MINUTES

    public static class LocalCacheConfigsBuilder {

        private int cacheSize;
        private int ttl = 5; // Override for default

        public LocalCacheConfigsBuilder ttl(int ttl) {
            this.ttl = ttl;
            return this;
        }
    }
}
