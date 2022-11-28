import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

/**
 * Extend this class to provide your own custom listener hook which will get triggered when any key gets removed.
 * @param <K>
 * @param <V>
 */
public abstract class KeyRemovalListenerHook<K, V> implements RemovalListener<K, V> {

    public abstract void actionOnRemove();

    @Override
    public void onRemoval(RemovalNotification<K, V> removalNotification) {
        actionOnRemove();
    }
}
