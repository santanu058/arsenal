import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import lombok.extern.slf4j.Slf4j;

/**
 * Extend this class to provide your own custom implementation(overriding actionOnRemove method) which will get triggered when any
 * key gets evicted.
 * @param <K>
 * @param <V>
 */
@Slf4j
public abstract class KeyRemovalListenerHook<K, V> implements RemovalListener<K, V> {

    public abstract void actionOnRemove(K key, V value);

    @Override
    public void onRemoval(K key, V value, RemovalCause removalCause) {
        log.debug("Key " + key + " with value " + value + " removed due to " + removalCause.name() + " action");
        actionOnRemove(key, value);
    }
}
