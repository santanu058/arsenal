package service.redis;

import com.google.inject.Singleton;
import lombok.Builder;
import lombok.Getter;
import redis.clients.jedis.JedisCluster;
import service.FallbackService;

import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class RedisFallbackService<V> extends FallbackService<V> {
    private static JedisCluster jedis;

    // Support Redis as a fallback cache for cache miss scenarios
    public static void fallbackFromRedis(Set<HostAndPort> nodes) {
        jedis = new JedisCluster(nodes.stream()
                .map(x -> new redis.clients.jedis.HostAndPort(x.getHost(), x.getPort()))
                .collect(Collectors.toSet()));

    }

    public static void fallbackFromRedis(Set<HostAndPort> nodes, RedisConfigs redisConfigs) {
        jedis = new JedisCluster(nodes.stream()
                .map(x -> new redis.clients.jedis.HostAndPort(x.getHost(), x.getPort()))
                .collect(Collectors.toSet()), redisConfigs.getTimeout(), redisConfigs.getMaxAttempts());

    }

    @Override
    public V get(String key) {
        return (V) jedis.get(key);
    }

    @Builder
    @Getter
    public static class HostAndPort {
        private String host;
        private int port;
    }

}
