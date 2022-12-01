import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.inject.Singleton;
import service.FallbackService;
import service.LocalCacheConfigs;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Singleton
public class SimpleCacheManager<K, V> implements ICacheManager<K, V> {

    private final LoadingCache<K, V> inMemCache;
    private FallbackService<V> fallbackService;


    /**
     * Executes the operation with defined cacheSize and default expiry time of 1 hour.
     * @param localCacheConfigs
     * @param fallbackService
     */
    public SimpleCacheManager(LocalCacheConfigs localCacheConfigs, FallbackService<V> fallbackService) {
        inMemCache = Caffeine.newBuilder()
                .maximumSize(localCacheConfigs.getCacheSize())
                .expireAfterWrite(localCacheConfigs.getTtl(), TimeUnit.HOURS)
                .build(
                        new CacheLoader<K, V>() {
                            @Override
                            public V load(K key) {
                                return inMemCache.get(key);
                            }
                        });
        this.fallbackService = fallbackService;
    }

    /**
     * Executes the operation with a trigger action on key removal from cache. Class can be provided with custom implementation.
     * @param localCacheConfigs
     * @param keyRemovalListenerHook
     * @param fallbackService
     */
    public SimpleCacheManager(LocalCacheConfigs localCacheConfigs, KeyRemovalListenerHook<K, V> keyRemovalListenerHook,
                              FallbackService<V> fallbackService) {
        inMemCache = Caffeine.newBuilder()
                .maximumSize(localCacheConfigs.getCacheSize())
                .expireAfterWrite(localCacheConfigs.getExpiryTime(), TimeUnit.MINUTES)
                .removalListener(keyRemovalListenerHook)
                .build(
                        new CacheLoader<K, V>() {
                            @Override
                            public V load(K key) {
                                return inMemCache.get(key);
                            }
                        });
        this.fallbackService = fallbackService;
    }

    @Override
    public void put(K key, V value) {
        inMemCache.put(key, value);
    }

    @Override
    public V get(K key) {
        if(inMemCache.getIfPresent(key) == null) {
            V result = fallbackService.get(String.valueOf(key));
            if(result != null) put(key, result);
        }
        return inMemCache.getIfPresent(key);
    }

    @Override
    public boolean checkIfKeyExists(K key) {
        return inMemCache.getIfPresent(key) != null;
    }

    @Override
    public void multiPut(Map<K, V> keyValues) {
        keyValues.forEach(inMemCache::put);
    }

    @Override
    public Map<K, V> multiGet(List<K> keys) {
        return keys.stream().collect(Collectors.toMap(k -> k, inMemCache::getIfPresent));
    }

    @Override
    public boolean removeKey(K key) {
        inMemCache.invalidate(key);
        return true;
    }

    @Override
    public boolean setIfNotExist(K key, V value, int ttlInSec) {
        return false;
    }

    @Override
    public void refreshCache() {
        /**
         * TO BE IMPLEMENTED
         * Purpose is to reload the data to reinit the cache here
         **/
    }
}
