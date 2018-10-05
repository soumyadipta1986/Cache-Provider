package com.opensource.cache.unbounded;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.opensource.cache.Cache;

/**
 * Copyright (c) 2018. Open source Project.
 * 
 * @author Soumyadipta Sarkar
 *
 * @param <K> The type of key maintained by the Cache
 * @param <V> The type of value maintained by the Cache
 * 
 * FastCache internally uses a HashMap to store the entries.
 * It is completely thread safe. It uses ReentrantReadWriteLock (Read Write pair lock).
 * It does not allow null key or value.
 */

public class FastCache<K, V> implements Cache<K, V> {
	
	private final Map<K, V> cache;
	private final Lock readLock;
	private final Lock writeLock;
	private final ReadWriteLock readWriteLock; 
	
	public FastCache(int initialCapacity) {
		cache = new HashMap<K, V>(initialCapacity);
		/*
		 * Not enforcing fairness because writer threads are supposed 
		 * to be out numbered by reader threads. Also a non-fair 
		 * ReentrantReadWriteLock is faster than the fair one. 
		 */
		readWriteLock = new ReentrantReadWriteLock();
		readLock = readWriteLock.readLock();
		writeLock = readWriteLock.writeLock();
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
	 * This method is thread safe and protected by Write lock.
	 * @see com.opensource.cache.Cache#add(java.lang.Object, java.lang.Object)
	 */
	@Override
	public V add(K key, V value) {
		checkNullKey(key);
		checkNullValue(value);
		writeLock.lock();
		try {
			return cache.put(key, value);
		} finally {
			writeLock.unlock();
		}
	}
	
	/*
	 * This method is thread safe and protected by Read lock.
	 * @see com.opensource.cache.Cache#retrieve(java.lang.Object)
	 */
	@Override
	public V retrieve(K key) {
		checkNullKey(key);
		readLock.lock();
		try {
			return cache.get(key);
		} finally {
			readLock.unlock();
		}
	}
	
	/*
	 * This method is thread safe and protected by Write lock. 
	 * Running time complexity of the method is O(n). 
	 * @see com.opensource.cache.Cache#clear()
	 */
	@Override
	public void clear() {
		writeLock.lock();
		try {
			cache.clear();
		} finally {
			writeLock.unlock();
		}
	}
	
	/*
	 * This method is thread safe and protected by Write lock. 
	 * @see com.opensource.cache.Cache#invalidateEntry(java.lang.Object)
	 */
	@Override
	public V invalidateEntry(K key) {
		checkNullKey(key);
		writeLock.lock();
		try {
			return cache.remove(key);
		} finally {
			writeLock.unlock();
		}
	}
	
	
	/*
	 * This method is thread safe and protected by Read lock.
	 * @see com.opensource.cache.Cache#size()
	 */
	@Override
	public int size() {
		readLock.lock();
		try {
			return cache.size();
		} finally {
			readLock.unlock();
		}
	}
	
	/*
	 * This method is not thread safe.
	 * @see com.opensource.cache.Cache#printCacheEntries()
	 */
	@Override
	public void printCacheEntries() {
		System.out.println("********* Fast Cache Entries *********");
		Set<Map.Entry<K, V>> entrySet = cache.entrySet();
		for (Map.Entry<K, V> entry : entrySet) {
			System.out.println(entry.getKey() + ",  " + entry.getValue());
		}
		System.out.println("*******************************");
	}

}
