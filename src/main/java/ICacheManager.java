public interface ICacheManager<K, V> {
    /**
     * Interface API for storing data into the local cache
     * @param key
     * @param value
     */
    void store(K key, V value);

    /**
     * Interface API for fetching data from the local cache
     * @param key
     */
    V fetch(K key);

    /**
     * Interface API for checking if a key exists in the local cache
     * @param key
     */

    boolean checkIfKeyExists(K key);
}
