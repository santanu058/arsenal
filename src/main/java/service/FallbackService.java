package service;

public interface FallbackService<K, V> {
    V getValue(K key);
    boolean put(K key, V Value);
}
