package org.redis4j.service;

import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.unify4j.model.c.Pair;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    /**
     * Stores a list of objects in Redis using the given RedisTemplate.
     * If the dispatch template is null, the list is empty, or the key is empty or blank, the method returns 0.
     *
     * @param dispatch The RedisTemplate used to store the list.
     * @param key      The key under which the list is stored.
     * @param list     The list of objects to store.
     * @param <T>      The type of objects in the list.
     * @return The number of elements in the list that were successfully stored; 0 if the operation failed or the inputs were invalid.
     */
    <T> long setCacheList(RedisTemplate<String, Object> dispatch, String key, List<T> list);

    /**
     * Retrieves a list of objects from Redis using the given RedisTemplate.
     * If the dispatch template is null, or if the key is empty or blank, the method returns an empty list.
     *
     * @param dispatch The RedisTemplate used to retrieve the list.
     * @param key      The key under which the list is stored.
     * @param <T>      The type of objects in the list.
     * @return The list of objects stored under the given key; an empty list if the operation failed or the inputs were invalid.
     */
    <T> List<T> getCacheList(RedisTemplate<String, Object> dispatch, String key);

    /**
     * Stores a set of objects in Redis using the given RedisTemplate and returns the BoundSetOperations for further operations.
     * If the dispatch template is null, the dataSet is empty, or the key is empty or blank, the method returns null.
     *
     * @param dispatch The RedisTemplate used to store the set.
     * @param key      The key under which the set is stored.
     * @param dataSet  The set of data to be stored.
     * @param <T>      The type of objects in the set.
     * @return The BoundSetOperations for the given key and set, or null if the operation failed or the inputs were invalid.
     */
    <T> BoundSetOperations<String, T> setCacheSet(RedisTemplate<String, Object> dispatch, String key, Set<T> dataSet);

    /**
     * Retrieves a set of objects from Redis using the given RedisTemplate and key.
     * If the dispatch template is null or the key is empty or blank, the method returns an empty set.
     *
     * @param dispatch The RedisTemplate used to retrieve the set.
     * @param key      The key under which the set is stored.
     * @param <T>      The type of objects in the set.
     * @return The set of objects retrieved from Redis, or an empty set if the operation failed or the inputs were invalid.
     */
    <T> Set<T> getCacheSet(RedisTemplate<String, Object> dispatch, String key);

    /**
     * Stores a map of objects in Redis using the given RedisTemplate and key.
     * If the dispatch template is null, the map is empty, or the key is empty or blank,
     * the method does nothing.
     *
     * @param dispatch The RedisTemplate used to store the map.
     * @param key      The key under which the map will be stored.
     * @param map      The map of objects to be stored in Redis.
     * @param <T>      The type of objects in the map.
     */
    <T> void setCacheMap(RedisTemplate<String, Object> dispatch, String key, Map<String, T> map);

    /**
     * Stores a map of objects in Redis using the given RedisTemplate and key.
     * If the dispatch template is null, the map is empty, or the key is empty or blank,
     * the method does nothing.
     *
     * @param dispatch The RedisTemplate used to store the map.
     * @param key      The key under which the map will be stored.
     * @param map      The map of objects to be stored in Redis.
     * @param <T>      The type of objects in the map.
     */
    @SuppressWarnings({"unused", "unchecked"})
    <T> void setCacheMapSafe(RedisTemplate<String, Object> dispatch, String key, Pair<String, T>... map);

    /**
     * Retrieves a map of objects from Redis using the given RedisTemplate and key.
     * If the dispatch template is null or the key is empty or blank,
     * the method returns an empty map.
     *
     * @param dispatch The RedisTemplate used to retrieve the map.
     * @param key      The key under which the map is stored.
     * @return A map of objects retrieved from Redis, or an empty map if the dispatch template or key is invalid.
     */
    Map<Object, Object> getCacheMap(RedisTemplate<String, Object> dispatch, String key);

    /**
     * Sets a value in a Redis hash using the given RedisTemplate, key, and hash key.
     * If the dispatch template is null, the value is null, or the key or hash key is empty or blank,
     * the method returns without performing any operation.
     *
     * @param dispatch The RedisTemplate used to set the hash value.
     * @param key      The key under which the hash is stored.
     * @param hKey     The hash key under which the value is stored.
     * @param value    The value to be set in the hash.
     */
    <T> void setCacheMapValue(RedisTemplate<String, Object> dispatch, String key, String hKey, T value);

    /**
     * Retrieves a value from a Redis hash using the given RedisTemplate, key, and hash key.
     * If the dispatch template is null, or if the key or hash key is empty or blank,
     * the method returns null.
     *
     * @param dispatch The RedisTemplate used to retrieve the hash value.
     * @param key      The key under which the hash is stored.
     * @param hKey     The hash key under which the value is stored.
     * @param <T>      The type of the value to be retrieved.
     * @return The value from the hash corresponding to the provided hash key, or null if not found.
     */
    <T> T getCacheMapValue(RedisTemplate<String, Object> dispatch, String key, String hKey);

    /**
     * Retrieves multiple values from a Redis hash using the given RedisTemplate, key, and collection of hash keys.
     * If the dispatch template is null, or if the key or collection of hash keys is empty or blank,
     * the method returns an empty list.
     *
     * @param dispatch The RedisTemplate used to retrieve the hash values.
     * @param key      The key under which the hash is stored.
     * @param hKeys    The collection of hash keys for which values need to be retrieved.
     * @param <T>      The type of the values to be retrieved.
     * @return A list of values from the hash corresponding to the provided hash keys, or an empty list if not found.
     */
    <T> List<T> getMultiCacheMapValue(RedisTemplate<String, Object> dispatch, String key, Collection<Object> hKeys);

    /**
     * Get list of basic objects of cache
     *
     * @return object list
     */
    Collection<String> defaultKeys(RedisTemplate<String, Object> dispatch);

    /**
     * Checks if a specific key exists in the Redis store using the given RedisTemplate.
     * If the dispatch template is null, or if the key is empty or blank, the method returns false.
     * Trims any whitespace from the key before checking its existence in the Redis store.
     *
     * @param dispatch The RedisTemplate used to check the existence of the key.
     * @param key      The key to check for existence in the Redis store.
     * @return true if the key exists in the Redis store; false otherwise.
     */
    boolean containsKey(RedisTemplate<String, Object> dispatch, String key);

    /**
     * Publishes data to a specified Redis topic using the given RedisTemplate.
     * If the dispatch template, topic, or data is null, the method returns without performing any action.
     * Attempts to send the data to the specified topic, and logs any exceptions that occur during the operation.
     *
     * @param dispatch The RedisTemplate used to send the data.
     * @param topic    The Redis topic to which the data is to be sent.
     * @param data     The data to be sent to the topic.
     * @param <T>      The type of data being sent.
     */
    <T> void produce(RedisTemplate<String, Object> dispatch, ChannelTopic topic, T data);

    /**
     * Increases the value of a numeric key in Redis.
     * If the dispatch template or key is null or empty, returns -1 indicating failure.
     * Uses Redis execute method to atomically increment the key value.
     * Logs any exceptions that occur during the operation.
     *
     * @param dispatch The RedisTemplate used to execute the operation.
     * @param key      The key whose value is to be incremented.
     * @return The incremented value of the key, or -1 if an error occurs.
     */
    long increaseKey(RedisTemplate<String, Object> dispatch, String key);

    /**
     * Decreases the value of a numeric key in Redis.
     * If the dispatch template or key is null or empty, returns -1 indicating failure.
     * Uses Redis execute method to atomically decrement the key value.
     * Logs any exceptions that occur during the operation.
     *
     * @param dispatch The RedisTemplate used to execute the operation.
     * @param key      The key whose value is to be decremented.
     * @return The decremented value of the key, or -1 if an error occurs.
     */
    long decreaseKey(RedisTemplate<String, Object> dispatch, String key);

    /**
     * Increases the value of a numeric key in Redis by a specified increment.
     * If the dispatch template, key, or increment value is invalid (null, empty, negative), returns -1 indicating failure.
     * Uses Redis execute method with a callback to atomically increment the key value by the specified amount.
     * Logs any exceptions that occur during the operation.
     *
     * @param dispatch The RedisTemplate used to execute the operation.
     * @param key      The key whose value is to be incremented.
     * @param value    The amount by which to increment the key's value.
     * @return The incremented value of the key, or -1 if an error occurs.
     */
    long increaseKeyBy(RedisTemplate<String, Object> dispatch, String key, long value);

    /**
     * Decreases the value of a numeric key in Redis by a specified decrement.
     * If the dispatch template, key, or decrement value is invalid (null, empty, negative), returns -1 indicating failure.
     * Uses Redis execute method with a callback to atomically decrement the key value by the specified amount.
     * Logs any exceptions that occur during the operation.
     *
     * @param dispatch The RedisTemplate used to execute the operation.
     * @param key      The key whose value is to be decremented.
     * @param value    The amount by which to decrement the key's value.
     * @return The decremented value of the key, or -1 if an error occurs.
     */
    long decreaseKeyBy(RedisTemplate<String, Object> dispatch, String key, long value);

    /**
     * Increases the value of a numeric key in Redis and sets an expiration time for the key.
     * If the dispatch template, key, timeout, or time unit is invalid (null, empty, negative), returns -1 indicating failure.
     * Uses the {@link #increaseKey} method to increment the key's value and then sets an expiration using the provided timeout and unit.
     *
     * @param dispatch The RedisTemplate used to execute the operation.
     * @param key      The key whose value is to be incremented and set with expiration.
     * @param timeout  The duration after which the key should expire.
     * @param unit     The time unit of the expiration timeout.
     * @return The incremented value of the key, or -1 if an error occurs.
     */
    long increaseKeyEx(RedisTemplate<String, Object> dispatch, String key, long timeout, TimeUnit unit);

    /**
     * Decreases the value of a numeric key in Redis and sets an expiration time for the key.
     * If the dispatch template, key, timeout, or time unit is invalid (null, empty, negative), returns -1 indicating failure.
     * Uses the {@link #decreaseKey} method to decrement the key's value and then sets an expiration using the provided timeout and unit.
     *
     * @param dispatch The RedisTemplate used to execute the operation.
     * @param key      The key whose value is to be decremented and set with expiration.
     * @param timeout  The duration after which the key should expire.
     * @param unit     The time unit of the expiration timeout.
     * @return The decremented value of the key, or -1 if an error occurs.
     */
    long decreaseKeyEx(RedisTemplate<String, Object> dispatch, String key, long timeout, TimeUnit unit);

    /**
     * Increases the value of a numeric key in Redis by a specified increment and sets an expiration time for the key.
     * If the dispatch template, key, timeout, unit, or value is invalid (null, empty, negative), returns -1 indicating failure.
     * Uses the {@link #increaseKeyBy} method to increment the key's value by the specified amount and then sets an expiration using the provided timeout and unit.
     *
     * @param dispatch The RedisTemplate used to execute the operation.
     * @param key      The key whose value is to be incremented and set with expiration.
     * @param value    The amount by which to increment the key's value.
     * @param timeout  The duration after which the key should expire.
     * @param unit     The time unit of the expiration timeout.
     * @return The incremented value of the key, or -1 if an error occurs.
     */
    long increaseKeyByEx(RedisTemplate<String, Object> dispatch, String key, long value, long timeout, TimeUnit unit);

    /**
     * Decreases the value of a numeric key in Redis by a specified decrement and sets an expiration time for the key.
     * If the dispatch template, key, timeout, unit, or value is invalid (null, empty, negative), returns -1 indicating failure.
     * Uses the {@link #decreaseKeyBy} method to decrement the key's value by the specified amount and then sets an expiration using the provided timeout and unit.
     *
     * @param dispatch The RedisTemplate used to execute the operation.
     * @param key      The key whose value is to be decremented and set with expiration.
     * @param value    The amount by which to decrement the key's value.
     * @param timeout  The duration after which the key should expire.
     * @param unit     The time unit of the expiration timeout.
     * @return The decremented value of the key, or -1 if an error occurs.
     */
    long decreaseKeyByEx(RedisTemplate<String, Object> dispatch, String key, long value, long timeout, TimeUnit unit);
}
