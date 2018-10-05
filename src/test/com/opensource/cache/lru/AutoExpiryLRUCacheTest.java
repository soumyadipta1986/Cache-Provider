package test.com.opensource.cache.lru;

import com.opensource.cache.Cache;
import com.opensource.cache.CacheFactory;

/**
 * Copyright (c) 2018. Open source Project.
 * 
 * @author Soumyadipta Sarkar
 * 
 * Test class to validate com.opensource.cache.lru.AutoExpiryLRUCache
 */
public class AutoExpiryLRUCacheTest {
	
	private void sleep(int milliSeconds) {
		try {
			Thread.sleep(milliSeconds);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}
	
	private void testAutoExpiryLRUCache(int expiryTimeInMilliSeconds) {
		CacheFactory<String, String> factory = new CacheFactory<String, String>();
		Cache<String, String> lruCache = factory.createAutoExpiryLRUCache(expiryTimeInMilliSeconds);
		lruCache.add("Key 01", "Value 01");
		sleep(2000);
		lruCache.add("Key 02", "Value 02");
		sleep(2000);
		lruCache.add("Key 03", "Value 03");
		lruCache.printCacheEntries();
		sleep(2000);
		lruCache.add("Key 02", "Value 02 Updated");
		lruCache.printCacheEntries();
		System.out.println("Key 03: " + lruCache.retrieve("Key 03"));
		/*
		 * Sleep for 15 seconds to see how cleanup thread removes expired entries from cache.
		 * Then JVM should be terminated as the cleanup thread is a daemon thread
		 */
		sleep(15000);
	}
	
	public static void main(String[] args) {
		AutoExpiryLRUCacheTest lruCacheTest = new AutoExpiryLRUCacheTest();  
		int expiryTimeInMilliSeconds = 5000;
		lruCacheTest.testAutoExpiryLRUCache(expiryTimeInMilliSeconds);
	}
	
}
