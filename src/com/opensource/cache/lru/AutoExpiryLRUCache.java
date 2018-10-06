package com.opensource.cache.lru;

import java.util.Date;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
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
 * AutoExpiryLRUCache clears the entries automatically from its internal HashMap if 
 * entries are not accessed for more than a certain amount of time (in milliseconds). 
 * Cleanup is done by a separate daemon thread. 
 * AutoExpiryLRUCache is unbounded and completely thread safe.
 * It uses ReentrantReadWriteLock (Read Write pair lock) to speed up the retrieval operation.  
 * This cache implementation does not allow null key or value.
 */

public class AutoExpiryLRUCache<K, V> implements Cache<K, V> {
	
	private final Lock readLock;
	private final Lock writeLock;
	private final ReadWriteLock readWriteLock;
	private final int expiryTimeInMilliSeconds;
	private final Map<K, ValueWrapper<V>> lruCache;
	
	public AutoExpiryLRUCache(int expiryTimeInMilliSeconds) {
		validateExpiryTime(expiryTimeInMilliSeconds);
		this.expiryTimeInMilliSeconds = expiryTimeInMilliSeconds;
		
		readWriteLock = new ReentrantReadWriteLock();
		readLock = readWriteLock.readLock();
		writeLock = readWriteLock.writeLock();
		lruCache = new HashMap<K, ValueWrapper<V>>();
		CacheCleaner cacheCleaner = new CacheCleaner();
		Thread cleanupThread = new Thread(cacheCleaner);
		cleanupThread.setDaemon(true); // Cleanup thread must be a daemon thread
		cleanupThread.start();
	}
	
	private void validateExpiryTime(int expiryTimeInMilliSeconds) {
		if (expiryTimeInMilliSeconds <= 0) {
			throw new IllegalArgumentException("Expiry time must be positive.");
		}
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
	
	/**
	 * Removes all the expired entries from Cache.
	 * This method is thread safe and protected by Write Lock.
	 * Running time complexity of the method is O(n).
	 */
	private void removeExpiredEntries() {
		System.out.println("*************** removeExpiredEntries starts **********************");
		writeLock.lock();
		try {
			System.out.println("Before cleanup, no. of entries in cache is " + lruCache.size());
			// Used Iterator instead of for loop to remove expired entries during iteration.
			Iterator<Map.Entry<K, ValueWrapper<V>>> cacheIterator = lruCache.entrySet().iterator(); 
			Date currentDate = new Date();
			System.out.println("Current time: " + currentDate);
			while (cacheIterator.hasNext()) {
				Map.Entry<K, ValueWrapper<V>> entry = cacheIterator.next();
				long timeDiff = currentDate.getTime() - entry.getValue().lastAccessedTime.getTime();
				if (timeDiff >= expiryTimeInMilliSeconds) {
					System.out.println(entry.getKey() + ", time diff in milliseconds: " + timeDiff + ", key has expired");
	 				cacheIterator.remove();
				} else {
					System.out.println(entry.getKey() + ", time diff in milliseconds: " + timeDiff + ", key is active");
				}
			}
			System.out.println("After cleanup, no. of entries in cache is " + lruCache.size());
		} finally {
			writeLock.unlock();
		}
		System.out.println("*************** removeExpiredEntries ends **********************");
	}
	
	/**
	 * Returns expiry time in milliSeconds after which Cache entries will
	 * get expired if not accessed.
	 *  
	 * @return expiryTimeInMilliSeconds Expiry time in milliSeconds
	 */
	public int getExpiryTimeInMilliSeconds() {
		return expiryTimeInMilliSeconds;
	}
	
	/*
	 * This method is thread safe and protected by Write Lock.  
	 * @see lru_cache.Cache#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public V add(K key, V value) {
		checkNullKey(key);
		checkNullValue(value);
		writeLock.lock();
		try {
			ValueWrapper<V> oldValueWrapper = lruCache.remove(key);
			ValueWrapper<V> newValueWrapper = new ValueWrapper<V>(value);
			lruCache.put(key, newValueWrapper);
			if (oldValueWrapper == null)
				return null;
			else 
				return oldValueWrapper.value;
		} finally {
			writeLock.unlock();
		}
	}
		
	/*
	 * This method is thread safe and protected by Read Lock.
	 * @see lru_cache.Cache#get(java.lang.Object)
	 */
	@Override
	public V retrieve(K key) {
		checkNullKey(key);
		readLock.lock();
		try {
			ValueWrapper<V> valueWrapper = lruCache.get(key);
			if (valueWrapper != null) {
				/*
				 * Even though the below line is a write operation, still we have 
				 * used Read Lock instead of a Write Lock. The main purpose of using
				 * Read Lock is to improve performance. Also, the write operation
				 * does not have to be synchronized as we don't need accurate time stamp.  
				 */
				valueWrapper.lastAccessedTime = new Date();
				return valueWrapper.value;
			}
			return null;
		} finally {
			readLock.unlock();
		}
	}
	
	/*
	 * This method is thread safe and protected by Write Lock.
	 * Running time complexity of the method is O(n).
	 * @see lru_cache.Cache#clear()
	 */
	@Override
	public void clear() {
		writeLock.lock();
		try {
			lruCache.clear();
		} finally {
			writeLock.unlock();
		}
	}
	
	/*
	 * This method is thread safe and protected by Write Lock. 
	 * @see lru_cache.Cache#invalidateEntry()
	 */
	@Override
	public V invalidateEntry(K key) {
		checkNullKey(key);
		writeLock.lock();
		try {
			ValueWrapper<V> valueWrapper = lruCache.remove(key);
			if (valueWrapper != null) {
				return valueWrapper.value;
			}
			return null;
		} finally {
			writeLock.unlock();
		}
	}
	
	/*
	 * This method is thread safe and protected by Read Lock.
	 * @see com.opensource.cache.Cache#size()
	 */
	@Override
	public int size() {
		readLock.lock();
		try {
			return lruCache.size();
		} finally {
			readLock.unlock();
		}		
	}
	
	
	/*
	 * This is a costly operation. Use it only for testing or debugging.
	 * This method is not thread safe.Running time complexity of the method is O(n).
	 * @see lru_cache.Cache#printCacheEntries()
	 */
	@Override
	public void printCacheEntries() {
		System.out.println("********* Cache Entries *********");
		Set<Map.Entry<K, ValueWrapper<V>>> entrySet = lruCache.entrySet();
		for (Map.Entry<K, ValueWrapper<V>> entry : entrySet) {
			ValueWrapper<V> valueWrapper = entry.getValue();
			System.out.println(entry.getKey() + ",  " + valueWrapper);
		}
		System.out.println("*******************************");
	}
	
	/**
	 * ValueWrapper is a nested static class that contains actual value 
	 * and the time stamp when the key was last accessed.
	 * 
	 * @param <V> The type of value maintained by the Cache
	 */
	private static class ValueWrapper<V> {
		
		private Date lastAccessedTime = new Date();
		private V value;
		
		private ValueWrapper(V value) {
			this.value = value;
		}
		
		@Override
		public String toString() {
			return "[" + value + " :: " + lastAccessedTime + "]";
		}
		
	}

	/**
	 * Inner class to remove all expired entries from cache.
	 */
	private class CacheCleaner implements Runnable {
		
		private CacheCleaner () {	
		}
		
		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(expiryTimeInMilliSeconds);
					removeExpiredEntries();
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		}
		
	}
	
}