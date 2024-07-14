package org.redis4j.common;

import org.redis4j.config.Redis4jBeanConfig;
import org.redis4j.config.Redis4jStatusConfig;
import org.redis4j.service.Redis4jConfigService;
import org.redis4j.service.Redis4jService;
import org.redis4j.service.impl.Redis4jConfigServiceImpl;
import org.redis4j.service.impl.Redis4jServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.unify4j.common.Object4j;

import java.util.Collection;
import java.util.Collections;
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
}
