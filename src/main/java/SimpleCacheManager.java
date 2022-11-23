import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Singleton;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Singleton
public class SimpleCacheManager<K, V> implements ICacheManager<K, V> {

    LoadingCache<K, V> inMemCache;

    /**
     * Executes the operation with defined cacheSize and default expiry time of 1 hour.
     * @param cacheSize
     */
    public SimpleCacheManager(int cacheSize) {
        inMemCache = CacheBuilder.newBuilder()
                .maximumSize(cacheSize)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build(
                        new CacheLoader<K, V>() {
                            @Override
                            public V load(K key) throws ExecutionException {
                                return inMemCache.get(key);
                            }
                        });
    }

    /**
     * Executes the operation with defined cacheSize and set expiry time.
     * @param cacheSize
     * @param expiryTime
     */
    public SimpleCacheManager(int cacheSize, int expiryTime) {
        inMemCache = CacheBuilder.newBuilder()
                .maximumSize(cacheSize)
                .expireAfterWrite(expiryTime, TimeUnit.MINUTES)
                .build(
                        new CacheLoader<K, V>() {
                            @Override
                            public V load(K key) throws ExecutionException {
                                return inMemCache.get(key);
                            }
                        });
    }

    /**
     * Executes the operation with a trigger action on key removal from cache. Class can provided with custom implementation.
     * @param cacheSize
     * @param expiryTime
     * @param keyRemovalListenerHook
     */
    public SimpleCacheManager(int cacheSize, int expiryTime, KeyRemovalListenerHook<K, V> keyRemovalListenerHook) {
        inMemCache = CacheBuilder.newBuilder()
                .maximumSize(cacheSize)
                .expireAfterWrite(expiryTime, TimeUnit.MINUTES)
                .removalListener(keyRemovalListenerHook)
                .build(
                        new CacheLoader<K, V>() {
                            @Override
                            public V load(K key) throws ExecutionException {
                                return inMemCache.get(key);
                            }
                        });
    }

    @Override
    public void store(K key, V value) {
        inMemCache.put(key, value);
    }

    @Override
    public V fetch(K key) {
        return inMemCache.getIfPresent(key);
    }

    @Override
    public boolean checkIfKeyExists(K key) {
        return inMemCache.getIfPresent(key) != null;
    }
}
