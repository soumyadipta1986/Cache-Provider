package com.opensource.cache;

/**
 * Copyright (c) 2018. Open source Project.
 * 
 * @author Soumyadipta Sarkar
 *
 * @param <K> The type of key maintained by the Cache
 * @param <V> The type of value maintained by the Cache
 * 
 * Interface to define methods that a Cache implementation should override.
 * 
 */
public interface Cache<K, V> {
	
	/**
	 * Adds an entry (key-value pair) to the Cache.
	 * 
	 * @param key key with which specified value is to be associated
	 * @param value value to be associated with the specified key
	 * @return the previous value associated with <tt>key</tt>, or
	 * <tt>null</tt> if there was no mapping for the <tt>key</tt>
	 * 
	 * @throws NullPointerException if the specified key or the value is null
	 */
	V add(K key, V value);
	
	/**
	 * Returns the value to which the specified key is associated,
     * or {@code null} if the Cache does not contain any mapping for the key.
	 * 
	 * @param key the key whose associated value is to be returned
	 * @return the value to which the specified key is associated or
     * {@code null} if the Cache does not have any entry for the key
     * 
     * @throws NullPointerException if the specified key is null
	 */
	V retrieve(K key);
	
	/**
	 * Removes all of the mappings from the Cache.
     * The Cache will be empty after this call returns.
	 */
	void clear();
	
	/**
	 * Removes the mapping for a key from the Cache if it is present.
     * 
	 * @param key key whose mapping is to be removed from the Cache
	 * @return the previous value associated with <tt>key</tt>, or
     * <tt>null</tt> if there was no mapping for <tt>key</tt>
	 * 
	 * @throws NullPointerException if the specified key is null
	 */
	V invalidateEntry(K key);
	
	/**
	 * Returns the number of entries present in the Cache.  
	 * If the Cache contains more than <tt>Integer.MAX_VALUE</tt> elements, 
	 * it returns <tt>Integer.MAX_VALUE</tt>.
     * 
	 * @return the number of entries in the Cache
	 */
	int size();
	
	
	/**
	 * This method has been added to validate the 
	 * correctness of different cache implementations.
	 * This must be removed/commented before production release.
	 */
	void printCacheEntries();

}