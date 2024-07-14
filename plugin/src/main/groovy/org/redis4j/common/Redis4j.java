package org.redis4j.common;

import org.redis4j.config.Redis4jBeanConfig;
import org.redis4j.config.Redis4jStatusConfig;
import org.redis4j.service.Redis4jConfigService;
import org.redis4j.service.Redis4jService;
import org.redis4j.service.impl.Redis4jConfigServiceImpl;
import org.redis4j.service.impl.Redis4jServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.unify4j.common.Object4j;
import org.unify4j.model.c.Pair;

import java.util.*;
import java.util.concurrent.TimeUnit;

public abstract class Redis4j {
    protected static final Logger logger = LoggerFactory.getLogger(Redis4j.class);
    protected static Redis4jService jService;
    protected static Redis4jConfigService service;
    private static Redis4jStatusConfig jStatusConfig;

    /**
     * Provides an instance of Redis4jStatusConfig.
     * If an instance is already available, returns it.
     * Otherwise, retrieves and returns a new instance using Redis4jBeanConfig.
     *
     * @return An instance of Redis4jStatusConfig, class {@link Redis4jStatusConfig}
     */
    protected static Redis4jStatusConfig jStatusConfigProvider() {
        if (Object4j.allNotNull(jStatusConfig)) {
            return jStatusConfig;
        }
        try {
            jStatusConfig = Redis4jBeanConfig.getBean(Redis4jStatusConfig.class);
        } catch (Exception ignored) {

        }
        return jStatusConfig;
    }

    /**
     * Provides an instance of Redis4jConfigService.
     * If an instance is already available, returns it.
     * Otherwise, retrieves and returns a new instance using Redis4jBeanConfig.
     *
     * @return An instance of Redis4jConfigService, class {@link Redis4jConfigService}
     */
    public static Redis4jConfigService provider() {
        if (Object4j.allNotNull(service)) {
            return service;
        }
        service = Redis4jBeanConfig.getBean(Redis4jConfigServiceImpl.class);
        return service;
    }

    /**
     * Provides an instance of Redis4jService.
     * If an instance is already available, returns it.
     * Otherwise, retrieves and returns a new instance using Redis4jBeanConfig.
     * In case of an exception during the retrieval, it is caught and ignored.
     *
     * @return An instance of Redis4jService, class {@link Redis4jService}
     */
    public static Redis4jService jProvider() {
        if (Object4j.allNotNull(jService)) {
            return jService;
        }
        try {
            jService = Redis4jBeanConfig.getBean(Redis4jServiceImpl.class);
        } catch (Exception ignored) {

        }
        return jService;
    }

    /**
     * Checks if the Redis configuration service is enabled.
     *
     * @return true if the service is enabled; false otherwise.
     */
    public static boolean isEnabled() {
        return provider().isEnabled();
    }

    /**
     * Checks if debugging is enabled in the Redis configuration service.
     *
     * @return true if debugging is enabled; false otherwise.
     */
    public static boolean isDebugging() {
        return provider().isDebugging();
    }

    /**
     * Checks if the Redis connection factory is connected.
     *
     * @return true if the factory is connected and can perform pipelined operations; false otherwise.
     */
    public static boolean isConnected() {
        Redis4jStatusConfig e = jStatusConfigProvider();
        if (e == null) {
            return false;
        }
        return e.isConnected();
    }

    /**
     * Checks if the Redis configuration service can be executed.
     * Returns true if the Redis connection factory is connected and the service is enabled.
     *
     * @return true if the service is executed; false otherwise.
     */
    @SuppressWarnings({"BooleanMethodIsAlwaysInverted"})
    public static boolean canExecuted() {
        Redis4jStatusConfig e = jStatusConfigProvider();
        if (e == null) {
            return false;
        }
        return e.canExecuted();
    }

    /**
     * Checks if the Redis dispatch template is available.
     *
     * @return true if the Redis dispatch template is initialized; false otherwise.
     */
    public static boolean isRedisDispatchAvailable() {
        Redis4jStatusConfig e = jStatusConfigProvider();
        if (e == null) {
            return false;
        }
        return e.isDispatchAvailable();
    }

    /**
     * Provides the Redis dispatch template.
     *
     * @return the RedisTemplate<String, Object> instance.
     */
    public static RedisTemplate<String, Object> dispatch() {
        Redis4jStatusConfig e = jStatusConfigProvider();
        if (e == null) {
            return null;
        }
        return e.dispatch();
    }

    /**
     * Provides the String dispatch template.
     *
     * @return the StringRedisTemplate instance.
     */
    public static StringRedisTemplate stringDispatch() {
        Redis4jStatusConfig e = jStatusConfigProvider();
        if (e == null) {
            return null;
        }
        return e.stringDispatch();
    }

    /**
     * Get list of basic objects of cache
     *
     * @param pattern string prefix
     * @return object list
     */
    public static Collection<String> keys(String pattern) {
        Redis4jService e = jProvider();
        if (e == null) {
            return Collections.emptyList();
        }
        return e.keys(dispatch(), pattern);
    }

    /**
     * Sets a cache object in Redis using the given RedisTemplate.
     * If the dispatch template or value is null, or if the key is empty or blank, the method returns without performing any operation.
     *
     * @param key   The key under which the value should be stored.
     * @param value The value to be cached.
     * @param <T>   The type of the value being cached.
     */
    public static <T> void setCacheObject(String key, T value) {
        Redis4jService e = jProvider();
        if (e == null) {
            return;
        }
        e.setCacheObject(dispatch(), key, value);
    }

    /**
     * Sets a cache object in Redis using the given RedisTemplate.
     * If the dispatch template or value is null, or if the key is empty or blank, the method returns without performing any operation.
     *
     * @param key   The key under which the value should be stored.
     * @param value The value to be cached.
     * @param <T>   The type of the value being cached.
     */
    public static <T> void canSetCacheObject(String key, T value) {
        if (!canExecuted()) {
            return;
        }
        setCacheObject(key, value);
    }

    /**
     * Sets a cache object in Redis with an expiration timeout using the given RedisTemplate.
     * If the dispatch template, value, or time unit is null, or if the timeout is negative,
     * or if the key is empty or blank, the method returns without performing any operation.
     *
     * @param key     The key under which the value should be stored.
     * @param value   The value to be cached.
     * @param timeout The expiration timeout for the cached object.
     * @param unit    The time unit for the expiration timeout.
     * @param <T>     The type of the value being cached.
     */
    public static <T> void setCacheObject(String key, T value, long timeout, TimeUnit unit) {
        Redis4jService e = jProvider();
        if (e == null) {
            return;
        }
        e.setCacheObject(dispatch(), key, value, timeout, unit);
    }

    /**
     * Sets a cache object in Redis with an expiration timeout using the given RedisTemplate.
     * If the dispatch template, value, or time unit is null, or if the timeout is negative,
     * or if the key is empty or blank, the method returns without performing any operation.
     *
     * @param key     The key under which the value should be stored.
     * @param value   The value to be cached.
     * @param timeout The expiration timeout for the cached object.
     * @param unit    The time unit for the expiration timeout.
     * @param <T>     The type of the value being cached.
     */
    public static <T> void canSetCacheObject(String key, T value, long timeout, TimeUnit unit) {
        if (!canExecuted()) {
            return;
        }
        setCacheObject(key, value, timeout, unit);
    }

    /**
     * Sets an expiration timeout on a cache object in Redis using the given RedisTemplate.
     * If the dispatch template, time unit is null, or if the timeout is negative,
     * or if the key is empty or blank, the method returns false.
     *
     * @param key     The key of the cache object on which the timeout should be set.
     * @param timeout The expiration timeout for the cache object.
     * @param unit    The time unit for the expiration timeout.
     * @return true if the expiration timeout was successfully set; false otherwise.
     */
    public static boolean expire(String key, long timeout, TimeUnit unit) {
        Redis4jService e = jProvider();
        if (e == null) {
            return false;
        }
        return e.expire(dispatch(), key, timeout, unit);
    }

    /**
     * Sets an expiration timeout on a cache object in Redis using the given RedisTemplate.
     * If the dispatch template, time unit is null, or if the timeout is negative,
     * or if the key is empty or blank, the method returns false.
     *
     * @param key     The key of the cache object on which the timeout should be set.
     * @param timeout The expiration timeout for the cache object.
     * @param unit    The time unit for the expiration timeout.
     * @return true if the expiration timeout was successfully set; false otherwise.
     */
    public static boolean canExpire(String key, long timeout, TimeUnit unit) {
        if (!canExecuted()) {
            return false;
        }
        return expire(key, timeout, unit);
    }

    /**
     * Retrieves a cache object from Redis using the given RedisTemplate.
     * If the dispatch template is null, or if the key is empty or blank, the method returns null.
     *
     * @param key The key of the cache object to retrieve.
     * @param <T> The type of the value being retrieved.
     * @return The cached object associated with the given key, or null if the dispatch template is null or the key is empty/blank.
     */
    public static <T> T getCacheObject(String key) {
        Redis4jService e = jProvider();
        if (e == null) {
            return null;
        }
        return e.getCacheObject(dispatch(), key);
    }

    /**
     * Retrieves a cache object from Redis using the given RedisTemplate.
     * If the dispatch template is null, or if the key is empty or blank, the method returns null.
     *
     * @param key The key of the cache object to retrieve.
     * @param <T> The type of the value being retrieved.
     * @return The cached object associated with the given key, or null if the dispatch template is null or the key is empty/blank.
     */
    public static <T> T canGetCacheObject(String key) {
        if (!canExecuted()) {
            return null;
        }
        return getCacheObject(key);
    }

    /**
     * Removes a cache object from Redis using the given RedisTemplate.
     * If the dispatch template is null, or if the key is empty or blank, the method returns false.
     *
     * @param key The key of the cache object to remove.
     * @return true if the cache object was successfully removed; false otherwise.
     */
    public static boolean removeObject(String key) {
        Redis4jService e = jProvider();
        if (e == null) {
            return false;
        }
        return e.removeObject(dispatch(), key);
    }

    /**
     * Removes a cache object from Redis using the given RedisTemplate.
     * If the dispatch template is null, or if the key is empty or blank, the method returns false.
     *
     * @param key The key of the cache object to remove.
     * @return true if the cache object was successfully removed; false otherwise.
     */
    public static boolean canRemoveObject(String key) {
        if (!canExecuted()) {
            return false;
        }
        return removeObject(key);
    }

    /**
     * Stores a list of objects in Redis using the given RedisTemplate.
     * If the dispatch template is null, the list is empty, or the key is empty or blank, the method returns 0.
     *
     * @param key  The key under which the list is stored.
     * @param list The list of objects to store.
     * @param <T>  The type of objects in the list.
     * @return The number of elements in the list that were successfully stored; 0 if the operation failed or the inputs were invalid.
     */
    public static <T> long setCacheList(String key, List<T> list) {
        Redis4jService e = jProvider();
        if (e == null) {
            return -1;
        }
        return e.setCacheList(dispatch(), key, list);
    }

    /**
     * Stores a list of objects in Redis using the given RedisTemplate.
     * If the dispatch template is null, the list is empty, or the key is empty or blank, the method returns 0.
     *
     * @param key  The key under which the list is stored.
     * @param list The list of objects to store.
     * @param <T>  The type of objects in the list.
     * @return The number of elements in the list that were successfully stored; 0 if the operation failed or the inputs were invalid.
     */
    public static <T> long canSetCacheList(String key, List<T> list) {
        if (!canExecuted()) {
            return -1;
        }
        return setCacheList(key, list);
    }

    /**
     * Retrieves a list of objects from Redis using the given RedisTemplate.
     * If the dispatch template is null, or if the key is empty or blank, the method returns an empty list.
     *
     * @param key The key under which the list is stored.
     * @param <T> The type of objects in the list.
     * @return The list of objects stored under the given key; an empty list if the operation failed or the inputs were invalid.
     */
    public static <T> List<T> getCacheList(String key) {
        Redis4jService e = jProvider();
        if (e == null) {
            return Collections.emptyList();
        }
        return e.getCacheList(dispatch(), key);
    }

    /**
     * Retrieves a list of objects from Redis using the given RedisTemplate.
     * If the dispatch template is null, or if the key is empty or blank, the method returns an empty list.
     *
     * @param key The key under which the list is stored.
     * @param <T> The type of objects in the list.
     * @return The list of objects stored under the given key; an empty list if the operation failed or the inputs were invalid.
     */
    public static <T> List<T> canGetCacheList(String key) {
        if (!canExecuted()) {
            return Collections.emptyList();
        }
        return getCacheList(key);
    }

    /**
     * Stores a set of objects in Redis using the given RedisTemplate and returns the BoundSetOperations for further operations.
     * If the dispatch template is null, the dataSet is empty, or the key is empty or blank, the method returns null.
     *
     * @param key     The key under which the set is stored.
     * @param dataSet The set of data to be stored.
     * @param <T>     The type of objects in the set.
     * @return The BoundSetOperations for the given key and set, or null if the operation failed or the inputs were invalid.
     */
    public static <T> BoundSetOperations<String, T> setCacheSet(String key, Set<T> dataSet) {
        Redis4jService e = jProvider();
        if (e == null) {
            return null;
        }
        return e.setCacheSet(dispatch(), key, dataSet);
    }

    /**
     * Stores a set of objects in Redis using the given RedisTemplate and returns the BoundSetOperations for further operations.
     * If the dispatch template is null, the dataSet is empty, or the key is empty or blank, the method returns null.
     *
     * @param key     The key under which the set is stored.
     * @param dataSet The set of data to be stored.
     * @param <T>     The type of objects in the set.
     * @return The BoundSetOperations for the given key and set, or null if the operation failed or the inputs were invalid.
     */
    public static <T> BoundSetOperations<String, T> canSetCacheSet(String key, Set<T> dataSet) {
        if (!canExecuted()) {
            return null;
        }
        return setCacheSet(key, dataSet);
    }

    /**
     * Retrieves a set of objects from Redis using the given RedisTemplate and key.
     * If the dispatch template is null or the key is empty or blank, the method returns an empty set.
     *
     * @param key The key under which the set is stored.
     * @param <T> The type of objects in the set.
     * @return The set of objects retrieved from Redis, or an empty set if the operation failed or the inputs were invalid.
     */
    public static <T> Set<T> getCacheSet(String key) {
        Redis4jService e = jProvider();
        if (e == null) {
            return Collections.emptySet();
        }
        return e.getCacheSet(dispatch(), key);
    }

    /**
     * Retrieves a set of objects from Redis using the given RedisTemplate and key.
     * If the dispatch template is null or the key is empty or blank, the method returns an empty set.
     *
     * @param key The key under which the set is stored.
     * @param <T> The type of objects in the set.
     * @return The set of objects retrieved from Redis, or an empty set if the operation failed or the inputs were invalid.
     */
    public static <T> Set<T> canGetCacheSet(String key) {
        if (!canExecuted()) {
            return Collections.emptySet();
        }
        return getCacheSet(key);
    }

    /**
     * Stores a map of objects in Redis using the given RedisTemplate and key.
     * If the dispatch template is null, the map is empty, or the key is empty or blank,
     * the method does nothing.
     *
     * @param key The key under which the map will be stored.
     * @param map The map of objects to be stored in Redis.
     * @param <T> The type of objects in the map.
     */
    public static <T> void setCacheMap(String key, Map<String, T> map) {
        Redis4jService e = jProvider();
        if (e == null) {
            return;
        }
        e.setCacheMap(dispatch(), key, map);
    }

    /**
     * Stores a map of objects in Redis using the given RedisTemplate and key.
     * If the dispatch template is null, the map is empty, or the key is empty or blank,
     * the method does nothing.
     *
     * @param key The key under which the map will be stored.
     * @param map The map of objects to be stored in Redis.
     * @param <T> The type of objects in the map.
     */
    public static <T> void canSetCacheMap(String key, Map<String, T> map) {
        if (!canExecuted()) {
            return;
        }
        setCacheMap(key, map);
    }

    /**
     * Stores a map of objects in Redis using the given RedisTemplate and key.
     * If the dispatch template is null, the map is empty, or the key is empty or blank,
     * the method does nothing.
     *
     * @param key The key under which the map will be stored.
     * @param map The map of objects to be stored in Redis.
     * @param <T> The type of objects in the map.
     */
    @SuppressWarnings({"unused", "unchecked"})
    public static <T> void setCacheMapSafe(String key, Pair<String, T>... map) {
        Redis4jService e = jProvider();
        if (e == null) {
            return;
        }
        e.setCacheMapSafe(dispatch(), key, map);
    }

    /**
     * Stores a map of objects in Redis using the given RedisTemplate and key.
     * If the dispatch template is null, the map is empty, or the key is empty or blank,
     * the method does nothing.
     *
     * @param key The key under which the map will be stored.
     * @param map The map of objects to be stored in Redis.
     * @param <T> The type of objects in the map.
     */
    @SuppressWarnings({"unused", "unchecked"})
    public static <T> void canSetCacheMapSafe(String key, Pair<String, T>... map) {
        if (!canExecuted()) {
            return;
        }
        setCacheMapSafe(key, map);
    }

    /**
     * Retrieves a map of objects from Redis using the given RedisTemplate and key.
     * If the dispatch template is null or the key is empty or blank,
     * the method returns an empty map.
     *
     * @param key The key under which the map is stored.
     * @return A map of objects retrieved from Redis, or an empty map if the dispatch template or key is invalid.
     */
    public static Map<Object, Object> getCacheMap(String key) {
        Redis4jService e = jProvider();
        if (e == null) {
            return Collections.emptyMap();
        }
        return e.getCacheMap(dispatch(), key);
    }

    /**
     * Retrieves a map of objects from Redis using the given RedisTemplate and key.
     * If the dispatch template is null or the key is empty or blank,
     * the method returns an empty map.
     *
     * @param key The key under which the map is stored.
     * @return A map of objects retrieved from Redis, or an empty map if the dispatch template or key is invalid.
     */
    public static Map<Object, Object> canGetCacheMap(String key) {
        if (!canExecuted()) {
            return Collections.emptyMap();
        }
        return getCacheMap(key);
    }

    /**
     * Sets a value in a Redis hash using the given RedisTemplate, key, and hash key.
     * If the dispatch template is null, the value is null, or the key or hash key is empty or blank,
     * the method returns without performing any operation.
     *
     * @param key   The key under which the hash is stored.
     * @param hKey  The hash key under which the value is stored.
     * @param value The value to be set in the hash.
     */
    public static <T> void setCacheMapValue(String key, String hKey, T value) {
        Redis4jService e = jProvider();
        if (e == null) {
            return;
        }
        e.setCacheMapValue(dispatch(), key, hKey, value);
    }

    /**
     * Sets a value in a Redis hash using the given RedisTemplate, key, and hash key.
     * If the dispatch template is null, the value is null, or the key or hash key is empty or blank,
     * the method returns without performing any operation.
     *
     * @param key   The key under which the hash is stored.
     * @param hKey  The hash key under which the value is stored.
     * @param value The value to be set in the hash.
     */
    public static <T> void canSetCacheMapValue(String key, String hKey, T value) {
        if (!canExecuted()) {
            return;
        }
        setCacheMapValue(key, hKey, value);
    }

    /**
     * Retrieves a value from a Redis hash using the given RedisTemplate, key, and hash key.
     * If the dispatch template is null, or if the key or hash key is empty or blank,
     * the method returns null.
     *
     * @param key  The key under which the hash is stored.
     * @param hKey The hash key under which the value is stored.
     * @param <T>  The type of the value to be retrieved.
     * @return The value from the hash corresponding to the provided hash key, or null if not found.
     */
    public static <T> T getCacheMapValue(String key, String hKey) {
        Redis4jService e = jProvider();
        if (e == null) {
            return null;
        }
        return e.getCacheMapValue(dispatch(), key, hKey);
    }

    /**
     * Retrieves a value from a Redis hash using the given RedisTemplate, key, and hash key.
     * If the dispatch template is null, or if the key or hash key is empty or blank,
     * the method returns null.
     *
     * @param key  The key under which the hash is stored.
     * @param hKey The hash key under which the value is stored.
     * @param <T>  The type of the value to be retrieved.
     * @return The value from the hash corresponding to the provided hash key, or null if not found.
     */
    public static <T> T canGetCacheMapValue(String key, String hKey) {
        if (!canExecuted()) {
            return null;
        }
        return getCacheMapValue(key, hKey);
    }

    /**
     * Retrieves multiple values from a Redis hash using the given RedisTemplate, key, and collection of hash keys.
     * If the dispatch template is null, or if the key or collection of hash keys is empty or blank,
     * the method returns an empty list.
     *
     * @param key   The key under which the hash is stored.
     * @param hKeys The collection of hash keys for which values need to be retrieved.
     * @param <T>   The type of the values to be retrieved.
     * @return A list of values from the hash corresponding to the provided hash keys, or an empty list if not found.
     */
    public static <T> List<T> getMultiCacheMapValue(String key, Collection<Object> hKeys) {
        Redis4jService e = jProvider();
        if (e == null) {
            return Collections.emptyList();
        }
        return e.getMultiCacheMapValue(dispatch(), key, hKeys);
    }

    public static Collection<String> defaultKeys() {
        Redis4jService e = jProvider();
        if (e == null) {
            return Collections.emptyList();
        }
        return e.defaultKeys(dispatch());
    }

    public static Collection<String> canDefaultKeys() {
        if (!canExecuted()) {
            return Collections.emptyList();
        }
        return defaultKeys();
    }

    /**
     * Checks if a specific key exists in the Redis store using the given RedisTemplate.
     * If the dispatch template is null, or if the key is empty or blank, the method returns false.
     * Trims any whitespace from the key before checking its existence in the Redis store.
     *
     * @param key The key to check for existence in the Redis store.
     * @return true if the key exists in the Redis store; false otherwise.
     */
    public static boolean containsKey(String key) {
        Redis4jService e = jProvider();
        if (e == null) {
            return false;
        }
        return e.containsKey(dispatch(), key);
    }

    /**
     * Publishes data to a specified Redis topic using the given RedisTemplate.
     * If the dispatch template, topic, or data is null, the method returns without performing any action.
     * Attempts to send the data to the specified topic, and logs any exceptions that occur during the operation.
     *
     * @param topic The Redis topic to which the data is to be sent.
     * @param data  The data to be sent to the topic.
     * @param <T>   The type of data being sent.
     */
    public static <T> void produce(ChannelTopic topic, T data) {
        Redis4jService e = jProvider();
        if (e == null) {
            return;
        }
        e.produce(dispatch(), topic, data);
    }

    /**
     * Publishes data to a specified Redis topic using the given RedisTemplate.
     * If the dispatch template, topic, or data is null, the method returns without performing any action.
     * Attempts to send the data to the specified topic, and logs any exceptions that occur during the operation.
     *
     * @param topic The Redis topic to which the data is to be sent.
     * @param data  The data to be sent to the topic.
     * @param <T>   The type of data being sent.
     */
    public static <T> void canProduce(ChannelTopic topic, T data) {
        if (!canExecuted()) {
            return;
        }
        produce(topic, data);
    }

    /**
     * Increases the value of a numeric key in Redis.
     * If the dispatch template or key is null or empty, returns -1 indicating failure.
     * Uses Redis execute method to atomically increment the key value.
     * Logs any exceptions that occur during the operation.
     *
     * @param key The key whose value is to be incremented.
     * @return The incremented value of the key, or -1 if an error occurs.
     */
    public static long increaseKey(String key) {
        Redis4jService e = jProvider();
        if (e == null) {
            return -1;
        }
        return e.increaseKey(dispatch(), key);
    }

    /**
     * Increases the value of a numeric key in Redis.
     * If the dispatch template or key is null or empty, returns -1 indicating failure.
     * Uses Redis execute method to atomically increment the key value.
     * Logs any exceptions that occur during the operation.
     *
     * @param key The key whose value is to be incremented.
     * @return The incremented value of the key, or -1 if an error occurs.
     */
    public static long canIncreaseKey(String key) {
        if (!canExecuted()) {
            return -1;
        }
        return increaseKey(key);
    }

    /**
     * Decreases the value of a numeric key in Redis.
     * If the dispatch template or key is null or empty, returns -1 indicating failure.
     * Uses Redis execute method to atomically decrement the key value.
     * Logs any exceptions that occur during the operation.
     *
     * @param key The key whose value is to be decremented.
     * @return The decremented value of the key, or -1 if an error occurs.
     */
    public static long decreaseKey(String key) {
        Redis4jService e = jProvider();
        if (e == null) {
            return -1;
        }
        return e.decreaseKey(dispatch(), key);
    }

    /**
     * Decreases the value of a numeric key in Redis.
     * If the dispatch template or key is null or empty, returns -1 indicating failure.
     * Uses Redis execute method to atomically decrement the key value.
     * Logs any exceptions that occur during the operation.
     *
     * @param key The key whose value is to be decremented.
     * @return The decremented value of the key, or -1 if an error occurs.
     */
    public static long canDecreaseKey(String key) {
        if (!canExecuted()) {
            return -1;
        }
        return decreaseKey(key);
    }

    /**
     * Increases the value of a numeric key in Redis by a specified increment.
     * If the dispatch template, key, or increment value is invalid (null, empty, negative), returns -1 indicating failure.
     * Uses Redis execute method with a callback to atomically increment the key value by the specified amount.
     * Logs any exceptions that occur during the operation.
     *
     * @param key   The key whose value is to be incremented.
     * @param value The amount by which to increment the key's value.
     * @return The incremented value of the key, or -1 if an error occurs.
     */
    public static long increaseKeyBy(String key, long value) {
        Redis4jService e = jProvider();
        if (e == null) {
            return -1;
        }
        return e.increaseKeyBy(dispatch(), key, value);
    }

    /**
     * Increases the value of a numeric key in Redis by a specified increment.
     * If the dispatch template, key, or increment value is invalid (null, empty, negative), returns -1 indicating failure.
     * Uses Redis execute method with a callback to atomically increment the key value by the specified amount.
     * Logs any exceptions that occur during the operation.
     *
     * @param key   The key whose value is to be incremented.
     * @param value The amount by which to increment the key's value.
     * @return The incremented value of the key, or -1 if an error occurs.
     */
    public static long canIncreaseKeyBy(String key, long value) {
        if (!canExecuted()) {
            return -1;
        }
        return increaseKeyBy(key, value);
    }

    /**
     * Decreases the value of a numeric key in Redis by a specified decrement.
     * If the dispatch template, key, or decrement value is invalid (null, empty, negative), returns -1 indicating failure.
     * Uses Redis execute method with a callback to atomically decrement the key value by the specified amount.
     * Logs any exceptions that occur during the operation.
     *
     * @param key   The key whose value is to be decremented.
     * @param value The amount by which to decrement the key's value.
     * @return The decremented value of the key, or -1 if an error occurs.
     */
    public static long decreaseKeyBy(String key, long value) {
        Redis4jService e = jProvider();
        if (e == null) {
            return -1;
        }
        return e.decreaseKeyBy(dispatch(), key, value);
    }

    /**
     * Decreases the value of a numeric key in Redis by a specified decrement.
     * If the dispatch template, key, or decrement value is invalid (null, empty, negative), returns -1 indicating failure.
     * Uses Redis execute method with a callback to atomically decrement the key value by the specified amount.
     * Logs any exceptions that occur during the operation.
     *
     * @param key   The key whose value is to be decremented.
     * @param value The amount by which to decrement the key's value.
     * @return The decremented value of the key, or -1 if an error occurs.
     */
    public static long canDecreaseKeyBy(String key, long value) {
        if (!canExecuted()) {
            return -1;
        }
        return decreaseKeyBy(key, value);
    }

    /**
     * Increases the value of a numeric key in Redis and sets an expiration time for the key.
     * If the dispatch template, key, timeout, or time unit is invalid (null, empty, negative), returns -1 indicating failure.
     * Uses the {@link #increaseKey} method to increment the key's value and then sets an expiration using the provided timeout and unit.
     *
     * @param key     The key whose value is to be incremented and set with expiration.
     * @param timeout The duration after which the key should expire.
     * @param unit    The time unit of the expiration timeout.
     * @return The incremented value of the key, or -1 if an error occurs.
     */
    public static long increaseKeyEx(String key, long timeout, TimeUnit unit) {
        Redis4jService e = jProvider();
        if (e == null) {
            return -1;
        }
        return e.increaseKeyEx(dispatch(), key, timeout, unit);
    }

    /**
     * Increases the value of a numeric key in Redis and sets an expiration time for the key.
     * If the dispatch template, key, timeout, or time unit is invalid (null, empty, negative), returns -1 indicating failure.
     * Uses the {@link #increaseKey} method to increment the key's value and then sets an expiration using the provided timeout and unit.
     *
     * @param key     The key whose value is to be incremented and set with expiration.
     * @param timeout The duration after which the key should expire.
     * @param unit    The time unit of the expiration timeout.
     * @return The incremented value of the key, or -1 if an error occurs.
     */
    public static long canIncreaseKeyEx(String key, long timeout, TimeUnit unit) {
        if (!canExecuted()) {
            return -1;
        }
        return increaseKeyEx(key, timeout, unit);
    }

    /**
     * Decreases the value of a numeric key in Redis and sets an expiration time for the key.
     * If the dispatch template, key, timeout, or time unit is invalid (null, empty, negative), returns -1 indicating failure.
     * Uses the {@link #decreaseKey} method to decrement the key's value and then sets an expiration using the provided timeout and unit.
     *
     * @param key     The key whose value is to be decremented and set with expiration.
     * @param timeout The duration after which the key should expire.
     * @param unit    The time unit of the expiration timeout.
     * @return The decremented value of the key, or -1 if an error occurs.
     */
    public static long decreaseKeyEx(String key, long timeout, TimeUnit unit) {
        Redis4jService e = jProvider();
        if (e == null) {
            return -1;
        }
        return e.decreaseKeyEx(dispatch(), key, timeout, unit);
    }

    /**
     * Decreases the value of a numeric key in Redis and sets an expiration time for the key.
     * If the dispatch template, key, timeout, or time unit is invalid (null, empty, negative), returns -1 indicating failure.
     * Uses the {@link #decreaseKey} method to decrement the key's value and then sets an expiration using the provided timeout and unit.
     *
     * @param key     The key whose value is to be decremented and set with expiration.
     * @param timeout The duration after which the key should expire.
     * @param unit    The time unit of the expiration timeout.
     * @return The decremented value of the key, or -1 if an error occurs.
     */
    public static long canDecreaseKeyEx(String key, long timeout, TimeUnit unit) {
        if (!canExecuted()) {
            return -1;
        }
        return decreaseKeyEx(key, timeout, unit);
    }

    /**
     * Increases the value of a numeric key in Redis by a specified increment and sets an expiration time for the key.
     * If the dispatch template, key, timeout, unit, or value is invalid (null, empty, negative), returns -1 indicating failure.
     * Uses the {@link #increaseKeyBy} method to increment the key's value by the specified amount and then sets an expiration using the provided timeout and unit.
     *
     * @param key     The key whose value is to be incremented and set with expiration.
     * @param value   The amount by which to increment the key's value.
     * @param timeout The duration after which the key should expire.
     * @param unit    The time unit of the expiration timeout.
     * @return The incremented value of the key, or -1 if an error occurs.
     */
    public static long increaseKeyByEx(String key, long value, long timeout, TimeUnit unit) {
        Redis4jService e = jProvider();
        if (e == null) {
            return -1;
        }
        return e.increaseKeyByEx(dispatch(), key, value, timeout, unit);
    }

    /**
     * Increases the value of a numeric key in Redis by a specified increment and sets an expiration time for the key.
     * If the dispatch template, key, timeout, unit, or value is invalid (null, empty, negative), returns -1 indicating failure.
     * Uses the {@link #increaseKeyBy} method to increment the key's value by the specified amount and then sets an expiration using the provided timeout and unit.
     *
     * @param key     The key whose value is to be incremented and set with expiration.
     * @param value   The amount by which to increment the key's value.
     * @param timeout The duration after which the key should expire.
     * @param unit    The time unit of the expiration timeout.
     * @return The incremented value of the key, or -1 if an error occurs.
     */
    public static long canIncreaseKeyByEx(String key, long value, long timeout, TimeUnit unit) {
        if (!canExecuted()) {
            return -1;
        }
        return increaseKeyByEx(key, value, timeout, unit);
    }

    /**
     * Decreases the value of a numeric key in Redis by a specified decrement and sets an expiration time for the key.
     * If the dispatch template, key, timeout, unit, or value is invalid (null, empty, negative), returns -1 indicating failure.
     * Uses the {@link #decreaseKeyBy} method to decrement the key's value by the specified amount and then sets an expiration using the provided timeout and unit.
     *
     * @param key     The key whose value is to be decremented and set with expiration.
     * @param value   The amount by which to decrement the key's value.
     * @param timeout The duration after which the key should expire.
     * @param unit    The time unit of the expiration timeout.
     * @return The decremented value of the key, or -1 if an error occurs.
     */
    public static long decreaseKeyByEx(String key, long value, long timeout, TimeUnit unit) {
        Redis4jService e = jProvider();
        if (e == null) {
            return -1;
        }
        return e.decreaseKeyByEx(dispatch(), key, value, timeout, unit);
    }

    /**
     * Decreases the value of a numeric key in Redis by a specified decrement and sets an expiration time for the key.
     * If the dispatch template, key, timeout, unit, or value is invalid (null, empty, negative), returns -1 indicating failure.
     * Uses the {@link #decreaseKeyBy} method to decrement the key's value by the specified amount and then sets an expiration using the provided timeout and unit.
     *
     * @param key     The key whose value is to be decremented and set with expiration.
     * @param value   The amount by which to decrement the key's value.
     * @param timeout The duration after which the key should expire.
     * @param unit    The time unit of the expiration timeout.
     * @return The decremented value of the key, or -1 if an error occurs.
     */
    public static long canDecreaseKeyByEx(String key, long value, long timeout, TimeUnit unit) {
        if (!canExecuted()) {
            return -1;
        }
        return decreaseKeyByEx(key, value, timeout, unit);
    }
}
