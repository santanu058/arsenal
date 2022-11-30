package service;

public abstract class FallbackService<V> {
    public abstract V get(String key);

}
