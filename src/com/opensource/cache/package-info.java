/**
 * Copyright (c) 2018. Open source Project.
 * 
 * @author Soumyadipta Sarkar
 * 
 * Open source cache implementation. This package contains Cache interface 
 * and CacheFactory class. Client code must use the factory class to get an
 * instance of type Cache.
 * 
 * Cache provider project mainly uses Factory Design Pattern.
 * 
 * Currently the factory class can create below Cache types:
 * 1. Fixed Capacity LRU Cache
 * 2. Auto Expiry LRU Cache
 * 3. Fast Unbounded Cache with Read Write pair lock
 * 4. Fast Unbounded Cache with Concurrent HashMap
 */
package com.opensource.cache;