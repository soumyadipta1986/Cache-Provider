package com.opensource.cache.unbounded;

import java.util.Set;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.opensource.cache.Cache;

/**
 * Copyright (c) 2018. Open source Project.
 *
 * @author Soumyadipta Sarkar
 *
 * @param <K> Cache Key type
 * @param <V> Cache Value type
 * 
 * This implementation internally uses ConcurrentHashMap to store cache entries.
 * It is thread safe. It does not allow null key or value.
 */
public class FastCacheWithConcurrentHashMap<K, V> implements Cache<K, V> {
	
	private final Map<K, V> cache;
	
	public FastCacheWithConcurrentHashMap(int initialCapacity) {
		/*
		 * Using default load factor (0.75f) and concurrency level (16).
		 * These parameters should not be hard coded. 
		 * Client programs should be able to configure them.
		 */
		cache = new ConcurrentHashMap<K, V>(initialCapacity, 0.75f, 16);
	}
	
	private void checkNullKey(K key) {
		if (key == null) {
			throw new NullPointerException("Key cannot be null.");
		}
	}
	
	private void checkNullValue(V value) {
		if (value == null) {
			throw new NullPointerException("Value cannot be null.");
		}
	}
	
	/*
	 * This method is thread safe. 
	 * Changes made to the cache is visible to all subsequent 
	 * reader and writer threads using happens-before relation.
	 * @see com.opensource.cache.Cache#add(java.lang.Object, java.lang.Object)
	 */
	@Override
	public V add(K key, V value) {
		checkNullKey(key);
		checkNullValue(value);
		return cache.put(key, value);
	}
	
	/*
	 * This method is thread safe. 
	 * Retrieval operations generally do not block, so may overlap 
	 * with update operations like add(), invalidateEntry() and clear(). 
	 * Retrievals reflect the results of the most recently completed update operations 
	 * holding upon their onset. More formally, an update operation for a given key bears 
	 * a happens-before relation with any retrieval for that key reporting the updated value.
	 * @see com.opensource.cache.Cache#retrieve(java.lang.Object)
	 */
	@Override
	public V retrieve(K key) {
		checkNullKey(key);
		return cache.get(key);
	}
	
	/*
	 * This method is thread safe.
	 * Changes made to the cache is visible to all subsequent 
	 * reader and writer threads using happens-before relation.
	 * Running time complexity of the method is O(n).
	 * @see com.opensource.cache.Cache#clear()
	 */
	@Override
	public void clear() {
		cache.clear();
	}
	
	/*
	 * This method is thread safe.
	 * Changes made to the cache is visible to all subsequent 
	 * reader and writer threads using happens-before relation.
	 * @see com.opensource.cache.Cache#invalidateEntry(java.lang.Object)
	 */
	@Override
	public V invalidateEntry(K key) {
		checkNullKey(key);
		return cache.remove(key);
	}
	
	/*
	 * This method is not thread safe.
	 * It may not reflect the changes made to the cache by another concurrent thread.
	 * This method is typically useful only when a map is not undergoing concurrent 
	 * updates in other threads. It can be used for monitoring or estimation purposes, 
	 * but not for program control.
	 * @see com.opensource.cache.Cache#size()
	 */
	@Override
	public int size() {
		return cache.size();
	}
	
	/*
	 * Changes to the map by other threads are reflected in this method. 
	 * @see com.opensource.cache.Cache#printCacheEntries()
	 */
	@Override
	public void printCacheEntries() {
		System.out.println("********* Fast Concurrent HashMap Cache Entries *********");
		Set<Map.Entry<K, V>> entrySet = cache.entrySet();
		for (Map.Entry<K, V> entry : entrySet) {
			System.out.println(entry.getKey() + ",  " + entry.getValue());
		}
		System.out.println("*******************************");
	}

}
