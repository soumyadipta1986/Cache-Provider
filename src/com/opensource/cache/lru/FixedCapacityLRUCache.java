package com.opensource.cache.lru;

import java.util.Set;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.opensource.cache.Cache;

/**
 * 
 * @author Soumyadipta Sarkar
 *
 * @param <K> Cache Key type
 * @param <V> Cache Value type
 * 
 * FixedCapacityLRUCache extends LinkedHashMap to restrict the maximum number of entries
 * at a given point of time. LinkedHashMap internally uses a HashMap and a doubly LinkedList (Queue).
 * FixedCapacityLRUCache uses super(maxCapacity, loadFactor, true) to make sure access order is maintained.
 * FixedCapacityLRUCache class is completely thread safe. It uses ReentrantLock to ensure thread safety.
 * This cache implementation does not allow null key or value.  
 */
public class FixedCapacityLRUCache<K, V> extends LinkedHashMap<K, V> implements Cache<K, V> {
	
	private static final long serialVersionUID = -5406158014470099600L;
	private final int maxCapacity; 
	/*
	 * We need Reentrant Lock because all operations include
	 * manipulation of the internal data structures i.e. HashMap and Doubly LinkedList.
	 */
	private final Lock lock = new ReentrantLock(); 
		
	public FixedCapacityLRUCache(int maxCapacity) {
		super(maxCapacity, 0.75f, true); // access order is set to true to ensure LRU principle
		this.maxCapacity = maxCapacity;
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
	
	/*
	 * This method removes the oldest entry when the cache size reaches it's max capacity.
	 * This method is thread safe. This method will always be invoked after adding an entry.
	 * @see java.util.LinkedHashMap#removeEldestEntry(java.util.Map.Entry)
	 */
	@Override
	protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
        lock.lock();
		try {
        	return size() > maxCapacity;
        } finally {
        	lock.unlock();
        }	
    }
	
	public int getMaxCapacity() {
		return maxCapacity;
	}
	
	/*
	 * This method is thread safe.
	 * This method throws IllegalArgumentException if either key or value is null.
	 * @see lru_cache.Cache#add(java.lang.Object, java.lang.Object)
	 */
	@Override
	public V add(K key, V value) {
		checkNullKey(key);
		checkNullValue(value);
		lock.lock();
		try {
			return super.put(key, value);
		} finally {
			lock.unlock();
		}		
	}
	
	/*
	 * This method is thread safe.
	 * This method throws IllegalArgumentException if key is null.
	 * @see lru_cache.Cache#retrieve(java.lang.Object)
	 */
	@Override
	public V retrieve(K key) {
		checkNullKey(key);
		lock.lock();
		try {
			return super.get(key);
		} finally {
			lock.unlock();
		}
	}
	
	/*
	 * This is a sensitive operation. It clears all the entries from the cache.
	 * This method is thread safe.
	 * Running time complexity of the method is O(n).
	 * @see java.util.LinkedHashMap#clear()
	 */
	@Override
	public void clear() {
		lock.lock();
		try {
			super.clear();
		} finally {
			lock.unlock();
		}
	}
	
	/*
	 * Call this method to remove an entry from the cache.
	 * This method is thread safe. 
	 * @see com.opensource.cache.Cache#invalidateEntry(java.lang.Object)
	 */
	@Override
	public V invalidateEntry(K key) {
		checkNullKey(key);
		lock.lock();
		try {
			return super.remove(key);
		} finally {
			lock.unlock();
		}
	}
	
	/*
	 * This method is thread safe.
	 * @see com.opensource.cache.Cache#size()
	 */
	@Override
	public int size() {
		lock.lock();
		try {
			return super.size();
		} finally {
			lock.unlock();
		}		
	}
	
	/*
	 * This method is a costly operation. Use it only for testing or debugging.
	 * This method is not thread safe.
	 * Running time complexity of the method is O(n).
	 * @see lru_cache.Cache#printCacheEntries()
	 */
	@Override
	public void printCacheEntries() {
		System.out.println("********* Cache Entries *********");
		Set<Map.Entry<K, V>> entrySet = super.entrySet();
		for (Map.Entry<K, V> entry : entrySet) {
			System.out.println(entry.getKey() + ",  " + entry.getValue());
		}
		System.out.println("*******************************");
	}

}