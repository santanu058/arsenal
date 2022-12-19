package service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class SimpleCacheManagerTest {

    ICacheManager<String, String> cacheManager;
    private FallbackService fallbackService;

    public SimpleCacheManagerTest() {
        fallbackService = Mockito.mock(FallbackService.class);
        cacheManager = new SimpleCacheManager<>(LocalCacheConfigs.builder().cacheSize(10).build(), fallbackService);
        Mockito.when(fallbackService.getValue("x")).thenReturn("DummyReturn");
    }

    @Test
    void testStore() {
        cacheManager.put("dummyKey", "dummyValue");
        Assertions.assertEquals(cacheManager.fetch("dummyKey"), "dummyValue");
    }

    @Test
    void testFetchWithCacheHit() {
        cacheManager.put("x", "dummy");
        Assertions.assertNotNull(cacheManager.fetch("x"));
    }

    @Test
    void testFetchWithCacheMiss() {
        Assertions.assertNull(cacheManager.fetch("missingKey"));
    }

    @Test
    void testFetchWithFetchOnCacheMiss() {
        Assertions.assertEquals(cacheManager.fetch("x"), "DummyReturn");
        Assertions.assertNotNull(cacheManager.fetch("x"));
    }

    @Test
    void testCheckIfExists() {
        cacheManager.put("dummyKey", "dummyValue");
        Assertions.assertTrue(cacheManager.checkIfKeyExists("dummyKey"));
    }

    @Test
    void testEvictionListenerHook() {
        KeyRemovalListenerHook<String, String> keyRemovalListenerHook = new KeyRemovalListenerHook<String, String>() {
            @Override
            public void actionOnRemove(String key, String value) {
                cacheManager.put("entryOnKeyRemoval", "true");
            }
        };
        LocalCacheConfigs localCacheConfigs = LocalCacheConfigs.builder()
                .cacheSize(10)
                .ttl(1)
                .build();
        ICacheManager<String, String> cacheManagerTest = new SimpleCacheManager<>(localCacheConfigs, keyRemovalListenerHook, fallbackService);
        cacheManagerTest.put("alice", "bob");
        cacheManagerTest.removeKey("alice");
        Assertions.assertNotNull(cacheManager.fetch("entryOnKeyRemoval"));
    }
}
