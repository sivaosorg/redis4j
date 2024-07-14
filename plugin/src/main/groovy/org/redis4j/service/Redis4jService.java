package org.redis4j.service;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public interface Redis4jService {

    /**
     * Get list of basic objects of cache
     *
     * @param pattern string prefix
     * @return object list
     */
    Collection<String> keys(RedisTemplate<String, Object> dispatch, String pattern);

    /**
     * Sets a cache object in Redis using the given RedisTemplate.
     * If the dispatch template or value is null, or if the key is empty or blank, the method returns without performing any operation.
     *
     * @param dispatch The RedisTemplate used to set the cache object.
     * @param key      The key under which the value should be stored.
     * @param value    The value to be cached.
     * @param <T>      The type of the value being cached.
     */
    <T> void setCacheObject(RedisTemplate<String, Object> dispatch, String key, T value);

    /**
     * Sets a cache object in Redis with an expiration timeout using the given RedisTemplate.
     * If the dispatch template, value, or time unit is null, or if the timeout is negative,
     * or if the key is empty or blank, the method returns without performing any operation.
     *
     * @param dispatch The RedisTemplate used to set the cache object.
     * @param key      The key under which the value should be stored.
     * @param value    The value to be cached.
     * @param timeout  The expiration timeout for the cached object.
     * @param unit     The time unit for the expiration timeout.
     * @param <T>      The type of the value being cached.
     */
    <T> void setCacheObject(RedisTemplate<String, Object> dispatch, String key, T value, long timeout, TimeUnit unit);

    /**
     * Sets an expiration timeout on a cache object in Redis using the given RedisTemplate.
     * If the dispatch template, time unit is null, or if the timeout is negative,
     * or if the key is empty or blank, the method returns false.
     *
     * @param dispatch The RedisTemplate used to set the expiration timeout.
     * @param key      The key of the cache object on which the timeout should be set.
     * @param timeout  The expiration timeout for the cache object.
     * @param unit     The time unit for the expiration timeout.
     * @return true if the expiration timeout was successfully set; false otherwise.
     */
    boolean expire(RedisTemplate<String, Object> dispatch, String key, long timeout, TimeUnit unit);

    /**
     * Retrieves a cache object from Redis using the given RedisTemplate.
     * If the dispatch template is null, or if the key is empty or blank, the method returns null.
     *
     * @param dispatch The RedisTemplate used to retrieve the cache object.
     * @param key      The key of the cache object to retrieve.
     * @param <T>      The type of the value being retrieved.
     * @return The cached object associated with the given key, or null if the dispatch template is null or the key is empty/blank.
     */
    <T> T getCacheObject(RedisTemplate<String, Object> dispatch, String key);

    /**
     * Removes a cache object from Redis using the given RedisTemplate.
     * If the dispatch template is null, or if the key is empty or blank, the method returns false.
     *
     * @param dispatch The RedisTemplate used to remove the cache object.
     * @param key      The key of the cache object to remove.
     * @return true if the cache object was successfully removed; false otherwise.
     */
    boolean removeObject(RedisTemplate<String, Object> dispatch, String key);
}
