import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class SimpleCacheManagerTest {

    ICacheManager<String, String> cacheManager = new SimpleCacheManager<>(10);

    @Test
    void testStore() {
        cacheManager.put("dummyKey", "dummyValue");
        Assertions.assertEquals(cacheManager.get("dummyKey"), "dummyValue");
    }

    @Test
    void testFetch() {
        Assertions.assertNull(cacheManager.get("x"));
    }

    @Test
    void testCheckIfExists() {
        cacheManager.put("dummyKey", "dummyValue");
        Assertions.assertTrue(cacheManager.checkIfKeyExists("dummyKey"));
    }
}
