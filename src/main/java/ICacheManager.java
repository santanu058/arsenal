import java.util.List;
import java.util.Map;

public interface ICacheManager<K, V> {
    /**
     * Interface API for storing data into the local cache
     * @param key
     * @param value
     */
    void put(K key, V value);

    /**
     * Interface API for fetching data from the local cache
     * @param key
     */
    V get(K key);

    /**
     * Interface API for checking if a key exists in the local cache
     * @param key
     */
    boolean checkIfKeyExists(K key);


    boolean removeKey(K key);

    void multiPut(Map<K, V> keyValues);

    Map<K, V> multiGet(List<K> keys);

    boolean setIfNotExist(K key, V value, int ttlInSec);

    void refreshCache();
}
