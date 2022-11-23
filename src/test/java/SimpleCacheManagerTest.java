import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class SimpleCacheManagerTest {

    ICacheManager<String, String> cacheManager = new SimpleCacheManager<>(10);

    @Test
    void testStore() {
        cacheManager.store("dummyKey", "dummyValue");
        Assertions.assertEquals(cacheManager.fetch("dummyKey"), "dummyValue");
    }

    @Test
    void testFetch() {
        Assertions.assertNull(cacheManager.fetch("x"));
    }

    @Test
    void testCheckIfExists() {
        cacheManager.store("dummyKey", "dummyValue");
        Assertions.assertTrue(cacheManager.checkIfKeyExists("dummyKey"));
    }
}
