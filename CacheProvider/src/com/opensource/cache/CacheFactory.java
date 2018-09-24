package com.opensource.cache;

import com.opensource.cache.lru.FixedCapacityLRUCache;
import com.opensource.cache.lru.AutoExpiryLRUCache;
import com.opensource.cache.unbounded.FastCache;

public class CacheFactory<K, V> {
	
	public Cache<K, V> createFixedCapacityLRUCache(int maxCapacity) {
		return new FixedCapacityLRUCache<K, V>(maxCapacity);
	}
	
	public Cache<K, V> createAutoExpiryLRUCache(int expiryTimeInMilliSeconds) {
		return new AutoExpiryLRUCache<K, V>(expiryTimeInMilliSeconds);		
	}
	
	public Cache<K, V> createFastUnboundedCache(int initialCapacity) {
		return new FastCache<K, V>(initialCapacity);
	}
	
}