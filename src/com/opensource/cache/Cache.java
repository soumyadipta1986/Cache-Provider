package com.opensource.cache;

public interface Cache<K, V> {
	
	V add(K key, V value);
	V retrieve(K key);
	void clear();
	V invalidateEntry(K key);
	int size();
	/*
	 * This method has been added to validate the 
	 * correctness of different cache implementations.
	 * This must be removed/commented before production release.
	 */
	void printCacheEntries();

}