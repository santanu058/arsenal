package service.db;

import service.FallbackService;

public class MysqlFallbackService<K, V> implements FallbackService<K, V> {

    @Override
    public Object getValue(Object key) {
        return null;
    }

    @Override
    public boolean put(Object key, Object Value) {
        return false;
    }
}
