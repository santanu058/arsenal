package service;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.inject.Singleton;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Singleton
public class SimpleCacheManager<K, V> implements ICacheManager<K, V> {

    private Map<Integer, LoadingCache<K, V>> dynamicTtlBasedCacheMap = new ConcurrentHashMap<>();
    private final FallbackService<K, V> fallbackService;
    private final LocalCacheConfigs localCacheConfigs;


    public SimpleCacheManager(LocalCacheConfigs localCacheConfigs) {
        this.localCacheConfigs = localCacheConfigs;
        if(!dynamicTtlBasedCacheMap.containsKey(localCacheConfigs.getTtl())) {
            dynamicTtlBasedCacheMap.put(
                    localCacheConfigs.getTtl(),
                    Caffeine.newBuilder()
                            .maximumSize(localCacheConfigs.getCacheSize())
                            .expireAfterWrite(localCacheConfigs.getTtl(), TimeUnit.HOURS)
                            .build(this::fetch)
            );
        }
        this.fallbackService = null;
    }

    /**
     * Executes the operation with defined cacheSize and default expiry time of 1 hour.
     * @param localCacheConfigs
     * @param fallbackService
     */
    public SimpleCacheManager(LocalCacheConfigs localCacheConfigs, FallbackService<K, V> fallbackService) {
        this.localCacheConfigs = localCacheConfigs;
        if(!dynamicTtlBasedCacheMap.containsKey(localCacheConfigs.getTtl())) {
            dynamicTtlBasedCacheMap.put(
                    localCacheConfigs.getTtl(),
                    Caffeine.newBuilder()
                            .maximumSize(localCacheConfigs.getCacheSize())
                            .expireAfterWrite(localCacheConfigs.getTtl(), TimeUnit.HOURS)
                            .build(this::fetch)
            );
        }
        this.fallbackService = fallbackService;
    }

    /**
     * Executes the operation with a trigger action on K removal from cache. Class can be provided with custom implementation.
     * @param localCacheConfigs
     * @param KRemovalListenerHook
     * @param fallbackService
     */
    public SimpleCacheManager(LocalCacheConfigs localCacheConfigs, KeyRemovalListenerHook<K, V> KRemovalListenerHook,
                              FallbackService<K, V> fallbackService) {
        this.localCacheConfigs = localCacheConfigs;
        if(!dynamicTtlBasedCacheMap.containsKey(localCacheConfigs.getTtl())) {
            dynamicTtlBasedCacheMap.put(
                    localCacheConfigs.getTtl(),
                    Caffeine.newBuilder()
                            .maximumSize(localCacheConfigs.getCacheSize())
                            .expireAfterWrite(localCacheConfigs.getTtl(), TimeUnit.HOURS)
                            .removalListener(KRemovalListenerHook)
                            .build(this::fetch)
            );
        }
        this.fallbackService = fallbackService;
    }

    @Override
    public void put(K k, V value) {
        dynamicTtlBasedCacheMap.get(localCacheConfigs.getTtl()).put(k, value);
    }

    @Override
    public void putWithTTL(K k, V value, int ttl) {
        if(!dynamicTtlBasedCacheMap.containsKey(ttl)) {
            dynamicTtlBasedCacheMap.put(
                    ttl,
                    Caffeine.newBuilder()
                            .maximumSize(localCacheConfigs.getCacheSize())
                            .expireAfterWrite(localCacheConfigs.getTtl(), TimeUnit.HOURS)
                            .build(this::fetch)
            );
        }
        dynamicTtlBasedCacheMap.get(ttl).put(k, value);
    }

    @Override
    public V fetch(K key) {
        if(dynamicTtlBasedCacheMap.get(localCacheConfigs.getTtl()).getIfPresent(key) == null) {
            if(fallbackService != null) {
                V result = (V) fallbackService.getValue(key);
                if(result != null) put(key, result);
            }
        }
        return dynamicTtlBasedCacheMap.get(localCacheConfigs.getTtl()).getIfPresent(key);
    }

    @Override
    public boolean checkIfKeyExists(K key) {
        return dynamicTtlBasedCacheMap.get(localCacheConfigs.getTtl()).getIfPresent(key) != null;
    }

    @Override
    public void multiPut(Map<K, V> KValues) {
        KValues.forEach(dynamicTtlBasedCacheMap.get(localCacheConfigs.getTtl())::put);
    }

    @Override
    public Map<K, V> multiGet(List<K> keys) {
        return keys.stream().collect(Collectors.toMap(k -> k, dynamicTtlBasedCacheMap.get(localCacheConfigs.getTtl())::getIfPresent));
    }

    @Override
    public boolean removeKey(K K) {
        dynamicTtlBasedCacheMap.get(localCacheConfigs.getTtl()).invalidate(K);
        return true;
    }

    @Override
    public boolean setIfNotExist(K K, V value, int ttlInSec) {
        put(K, value);
        return true;
    }

    @Override
    public void refreshCache() {
        /**
         * TO BE IMPLEMENTED
         * Purpose is to reload the data to reinit the cache here
         **/
    }
}
