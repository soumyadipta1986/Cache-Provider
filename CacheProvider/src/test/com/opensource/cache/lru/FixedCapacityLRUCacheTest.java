package test.com.opensource.cache.lru;

import com.opensource.cache.Cache;
import com.opensource.cache.CacheFactory;

public class FixedCapacityLRUCacheTest {
	
	private void testFixedCapacityLRUCache(int maxCapacity) {
		CacheFactory<String, String> factory = new CacheFactory<String, String>();
		Cache<String, String> lruCache = factory.createFixedCapacityLRUCache(maxCapacity);
		lruCache.add("Key 01", "Value 01");
		lruCache.add("Key 02", "Value 02");
		lruCache.add("Key 03", "Value 03");
		lruCache.printCacheEntries();
		lruCache.add("Key 02", "Value 02 Updated");
		lruCache.printCacheEntries();
		System.out.println("Key 03: " + lruCache.retrieve("Key 03"));
		lruCache.printCacheEntries();
		lruCache.add("Key 04", "Value 04");
		lruCache.printCacheEntries();
	}
	
	public static void main(String[] args) {
		FixedCapacityLRUCacheTest lruCacheTest = new FixedCapacityLRUCacheTest();
		int maxCapacity = 3;
		lruCacheTest.testFixedCapacityLRUCache(maxCapacity);
	}
	
}
