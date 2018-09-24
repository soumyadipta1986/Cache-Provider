package com.opensource.cache.unbounded;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.opensource.cache.Cache;

/**
 * 
 * @author Soumyadipta Sarkar
 * 
 * @param <K> Cache Key type
 * @param <V> Cache Value type
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
			throw new IllegalArgumentException("Key cannot be null.");
		}
	}
	
	private void checkNullValue(V value) {
		if (value == null) {
			throw new IllegalArgumentException("Value cannot be null.");
		}
	}
		
	/**
	 * This method is thread safe and protected by Write lock.
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
	
	/**
	 * This method is thread safe and protected by Read lock.
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
	
	/**
	 * This is a sensitive operation. It removes all the entries from cache.
	 * Time complexity of this method is O(n).
	 * This method is thread safe and protected by Write lock.
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
	
	/**
	 * Call this method to remove an entry from cache.
	 * This method is thread safe and protected by Write lock.
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
	
	/**
	 * This method is thread safe and protected by Read lock.
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
	 * This method has been added for testing purpose. It is not thread safe.
	 * This method should be removed or commented before production release.
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
