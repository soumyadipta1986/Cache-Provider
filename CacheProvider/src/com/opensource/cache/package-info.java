/**
 * Open source cache implementation. This package contains Cache interface 
 * and CacheFactory class. Client code must use the factory class to get an
 * instance of type Cache.
 * 
 * Cache provider project mainly uses Factory Design Pattern.
 * 
 * Currently the factory class can create below Cache types:
 * 1. Fixed Capacity LRU Cache
 * 2. Auto Expiry LRU Cache
 * 3. Fast Unbounded Cache
 * 
 * @author Soumyadipta Sarkar
 *
 */
package com.opensource.cache;