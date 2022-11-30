import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import service.FallbackService;


public class SimpleCacheManagerTest {

    ICacheManager<String, String> cacheManager;
    private FallbackService fallbackService;

    public SimpleCacheManagerTest() {
        fallbackService = Mockito.mock(FallbackService.class);
        cacheManager = new SimpleCacheManager<>(10, fallbackService);
        Mockito.when(fallbackService.get("x")).thenReturn("DummyReturn");
    }

    @Test
    void testStore() {
        cacheManager.put("dummyKey", "dummyValue");
        Assertions.assertEquals(cacheManager.get("dummyKey"), "dummyValue");
    }

    @Test
    void testFetchWithCacheHit() {
        cacheManager.put("x", "dummy");
        Assertions.assertNotNull(cacheManager.get("x"));
    }

    @Test
    void testFetchWithCacheMiss() {
        Assertions.assertNull(cacheManager.get("missingKey"));
    }

    @Test
    void testFetchWithFetchOnCacheMiss() {
        Assertions.assertEquals(cacheManager.get("x"), "DummyReturn");
        Assertions.assertNotNull(cacheManager.get("x"));
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
        ICacheManager<String, String> cacheManagerTest = new SimpleCacheManager<>(10, 1, keyRemovalListenerHook, fallbackService);
        cacheManagerTest.put("alice", "bob");
        cacheManagerTest.removeKey("alice");
        Assertions.assertNotNull(cacheManager.get("entryOnKeyRemoval"));
    }
}
