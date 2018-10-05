package com.opensource.cache;

import com.opensource.cache.lru.AutoExpiryLRUCache;
import com.opensource.cache.lru.FixedCapacityLRUCache;
import com.opensource.cache.unbounded.FastCache;
import com.opensource.cache.unbounded.FastCacheWithConcurrentHashMap;

/**
 * Copyright (c) 2018. Open source Project.
 * 
 * @author Soumyadipta Sarkar
 *
 * @param <K> The type of key maintained by the Cache
 * @param <V> The type of value maintained by the Cache
 * 
 * Factory class to create different types of Cache implementation.
 * 
 */
public class CacheFactory<K, V> {
	
	/**
	 * Creates a Least Recently Used (LRU) Cache with fixed capacity.  
	 * Once the Cache reaches its maximum size, adding any further entry
	 * to the Cache eventually removes the least recently used entry.
	 * This Cache implementation is thread safe. 
	 * 
	 * @param maxCapacity maximum capacity of the Cache 
	 * @return Fixed capacity LRU Cache
	 */
	public Cache<K, V> createFixedCapacityLRUCache(int maxCapacity) {
		return new FixedCapacityLRUCache<K, V>(maxCapacity);
	}
	
	/**
	 * Creates a Cache whose entries will be removed automatically if 
	 * they are not accessed for more than a fixed amount of time in milliseconds.
	 * This Cache implementation is thread safe.
	 * 
	 * @param expiryTimeInMilliSeconds time in milliseconds after which entries
	 * will be removed from Cache if not accessed 
	 * @return Auto Expiry LRU Cache
	 */
	public Cache<K, V> createAutoExpiryLRUCache(int expiryTimeInMilliSeconds) {
		return new AutoExpiryLRUCache<K, V>(expiryTimeInMilliSeconds);		
	}
	
	/**
	 * Creates an unbounded Cache with a given initial capacity.
	 * This Cache implementation is thread safe. It uses Read Write
	 * pair lock to enhance performance.
	 * 
	 * @param initialCapacity initial capacity of the Cache
	 * @return Unbounded Cache
	 */
	public Cache<K, V> createFastUnboundedCache(int initialCapacity) {
		return new FastCache<K, V>(initialCapacity);
	}
	
	/**
	 * Creates an unbounded Cache with a given initial capacity.
	 * This Cache implementation is thread safe. Internally
	 * it uses ConcurrentHashMap to store the entries. 
	 * 
	 * @param initialCapacity initial capacity of the Cache
	 * @return Unbounded Cache
	 */
	public Cache<K, V> createFastUnboundedCacheWithConcurrentHashMap(int initialCapacity) {
		return new FastCacheWithConcurrentHashMap<K, V>(initialCapacity);
	}
	
}