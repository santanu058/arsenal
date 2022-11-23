import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

public abstract class KeyRemovalListenerHook<K, V> implements RemovalListener<K, V> {

    public abstract void actionOnRemove();

    @Override
    public void onRemoval(RemovalNotification<K, V> removalNotification) {
        actionOnRemove();
    }
}
