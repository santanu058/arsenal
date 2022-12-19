package service.fallbacks;

import com.google.inject.Singleton;
import exceptions.IncompatibeDataTypeException;
import exceptions.InvalidNodeAddressFormat;
import lombok.Builder;
import lombok.Getter;
import redis.clients.jedis.JedisCluster;
import service.FallbackService;

import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class RedisFallbackService<K, V> implements FallbackService<K, V> {
    private static JedisCluster jedis;
    public RedisFallbackService(Set<HostAndPort> nodes) {
        if(nodes.isEmpty())
            throw new InvalidNodeAddressFormat("Valid cluster node IPs missing");
        fallbackFromRedis(nodes);
    }

    public RedisFallbackService(Set<HostAndPort> nodes, RedisConfigs redisConfigs) {
        if(nodes.isEmpty())
            throw new InvalidNodeAddressFormat("Valid cluster node IPs missing");
        fallbackFromRedis(nodes, redisConfigs);
    }

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
    public V getValue(K key) {
        if (!(key instanceof byte[] || key instanceof String))
            throw new IncompatibeDataTypeException("Redis acceptable formats are String OR byte[]");
        if (key instanceof byte[])
            return (V) getByte((byte[]) key);
        return (V) getString(String.valueOf(key));
    }

    private String getString(String key) {
        return jedis.get(key);
    }

    private byte[] getByte(byte[] key) {
        return jedis.get(key);
    }

    @Override
    public boolean put(K key, V Value) {
        // Not supported yet
        return false;
    }

    @Builder
    @Getter
    public static class HostAndPort {
        private String host;
        private int port;
    }

}
