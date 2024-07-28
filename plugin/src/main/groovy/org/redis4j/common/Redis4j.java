package org.redis4j.common;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import io.lettuce.core.api.sync.RedisCommands;
import org.redis4j.config.Redis4jBeanConfig;
import org.redis4j.config.Redis4jStatusConfig;
import org.redis4j.service.Redis4jConfigService;
import org.redis4j.service.Redis4jService;
import org.redis4j.service.Redis4jWrapCallback;
import org.redis4j.service.impl.Redis4jConfigServiceImpl;
import org.redis4j.service.impl.Redis4jServiceImpl;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.unify4j.common.Collection4j;
import org.unify4j.common.Object4j;
import org.unify4j.common.String4j;
import org.unify4j.common.UniqueId4j;
import org.unify4j.model.builder.HttpStatusBuilder;
import org.unify4j.model.builder.HttpWrapBuilder;
import org.unify4j.model.c.Pair;
import org.unify4j.model.response.WrapResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public abstract class Redis4j {
    protected static Redis4jService jService;
    protected static Redis4jConfigService service;
    protected static Redis4jStatusConfig jStatusConfig;
    protected static RedisClient client;
    protected static StatefulRedisConnection<String, String> connection;
    private static final Lock lock = new ReentrantLock();

    /**
     * @return the HTTP servlet request, class {@link HttpServletRequest}
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes s = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return s.getRequest();
    }

    /**
     * Retrieves the current session ID from the request context.
     * <p>
     * This method accesses the current request attributes from the RequestContextHolder
     * and extracts the session ID associated with the current request. This is useful
     * for tracking the session of the user making the request, especially in web
     * applications where session management is crucial for user authentication and
     * maintaining user state across multiple requests.
     *
     * @return the session ID of the current request, or null if no session is associated with the current request context
     */
    public static String getCurrentSessionId() {
        try {
            ServletRequestAttributes s = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            return s.getSessionId();
        } catch (IllegalStateException e) {
            return String.valueOf(UniqueId4j.getUniqueId19());
        }
    }

    /**
     * Retrieves the session ID from the given HttpServletRequest.
     * <p>
     * This method gets the current HttpSession associated with the request,
     * and then extracts the session ID from it. If there is no current session
     * and create is false, it returns null.
     *
     * @param request the HttpServletRequest from which to retrieve the session ID
     * @return the session ID, or null if there is no current session
     */
    public static String getSessionId(HttpServletRequest request) {
        if (request == null) {
            return String.valueOf(UniqueId4j.getUniqueId19());
        }
        HttpSession session = request.getSession(false); // Pass false to prevent creating a new session if one does not exist
        return (session != null) ? session.getId() : null;
    }

    /**
     * Provides an instance of Redis4jStatusConfig.
     * If an instance is already available, returns it.
     * Otherwise, retrieves and returns a new instance using Redis4jBeanConfig.
     *
     * @return An instance of Redis4jStatusConfig, class {@link Redis4jStatusConfig}
     */
    protected static Redis4jStatusConfig jStatusConfigProvider() {
        lock.lock();
        try {
            if (Object4j.allNotNull(jStatusConfig)) {
                return jStatusConfig;
            }
            try {
                jStatusConfig = Redis4jBeanConfig.getBean(Redis4jStatusConfig.class);
            } catch (Exception ignored) {

            }
            return jStatusConfig;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Provides an instance of Redis4jConfigService.
     * If an instance is already available, returns it.
     * Otherwise, retrieves and returns a new instance using Redis4jBeanConfig.
     *
     * @return An instance of Redis4jConfigService, class {@link Redis4jConfigService}
     */
    public static Redis4jConfigService provider() {
        lock.lock();
        try {
            if (Object4j.allNotNull(service)) {
                return service;
            }
            service = Redis4jBeanConfig.getBean(Redis4jConfigServiceImpl.class);
            return service;
        } finally {
            lock.unlock();
        }
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
        lock.lock();
        try {
            if (Object4j.allNotNull(jService)) {
                return jService;
            }
            try {
                jService = Redis4jBeanConfig.getBean(Redis4jServiceImpl.class);
            } catch (Exception ignored) {

            }
            return jService;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Provides an instance of RedisClient.
     * If an instance is already available, returns it.
     * Otherwise, creates and returns a new instance using the Redis URI.
     *
     * @return An instance of RedisClient, class {@link RedisClient}
     */
    public static RedisClient clientProvider() {
        lock.lock();
        try {
            if (Object4j.allNotNull(client)) {
                return client;
            }
            client = provider().clientProvider();
            return client;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Provides an instance of StatefulRedisConnection.
     * If an instance is already available, returns it.
     * Otherwise, creates and returns a new instance using the RedisClient.
     *
     * @return An instance of StatefulRedisConnection, class {@link StatefulRedisConnection}
     */
    public static StatefulRedisConnection<String, String> connectionProvider() {
        lock.lock();
        try {
            if (Object4j.allNotNull(connection)) {
                return connection;
            }
            RedisClient c = clientProvider();
            if (Object4j.allNotNull(c)) {
                connection = c.connect();
            }
            return connection;
        } finally {
            lock.unlock();
        }
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
     * Provides an instance of RedisCommands for synchronous operations.
     *
     * @return An instance of RedisCommands, class {@link RedisCommands}
     */
    public static RedisCommands<String, String> syncCommands() {
        return connectionProvider().sync();
    }

    /**
     * Provides an instance of RedisAsyncCommands for asynchronous operations.
     *
     * @return An instance of RedisAsyncCommands, class {@link RedisAsyncCommands}
     */
    public static RedisAsyncCommands<String, String> asyncCommands() {
        return connectionProvider().async();
    }

    /**
     * Provides an instance of RedisReactiveCommands for reactive operations.
     *
     * @return An instance of RedisReactiveCommands, class {@link RedisReactiveCommands}
     */
    public static RedisReactiveCommands<String, String> reactiveCommands() {
        return connectionProvider().reactive();
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
     * Get list of basic objects of cache
     *
     * @param pattern string prefix
     * @return object list
     */
    public static Collection<String> keys(String pattern, Redis4jWrapCallback callback) {
        Redis4jService e = jProvider();
        if (e == null) {
            return Collections.emptyList();
        }
        return e.keys(dispatch(), pattern, callback);
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
     * Sets a cache object in Redis using the given RedisTemplate, with an optional callback
     * for handling exceptions. If the dispatch template or value is null, or if the key is empty
     * or blank, the method returns without performing any operation.
     *
     * @param key      The key under which the value should be stored.
     * @param value    The value to be cached.
     * @param callback An optional callback for handling exceptions, an instance of {@link Redis4jWrapCallback}.
     * @param <T>      The type of the value being cached.
     */
    public static <T> void setCacheObject(String key, T value, Redis4jWrapCallback callback) {
        Redis4jService e = jProvider();
        if (e == null) {
            return;
        }
        e.setCacheObject(dispatch(), key, value, callback);
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
     * Sets a cache object in Redis with an expiration timeout using the given RedisTemplate, with an optional callback
     * for handling exceptions. If the dispatch template, value, or time unit is null, or if the timeout is negative,
     * or if the key is empty or blank, the method returns without performing any operation.
     *
     * @param key      The key under which the value should be stored.
     * @param value    The value to be cached.
     * @param timeout  The expiration timeout for the cached object.
     * @param unit     The time unit for the expiration timeout.
     * @param callback An optional callback for handling exceptions, an instance of {@link Redis4jWrapCallback}.
     * @param <T>      The type of the value being cached.
     */
    public static <T> void setCacheObject(String key, T value, long timeout, TimeUnit unit, Redis4jWrapCallback callback) {
        Redis4jService e = jProvider();
        if (e == null) {
            return;
        }
        e.setCacheObject(dispatch(), key, value, timeout, unit, callback);
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
     * Sets an expiration timeout on a cache object in Redis using the given RedisTemplate, with an optional callback
     * for handling exceptions. If the dispatch template, time unit is null, or if the timeout is negative,
     * or if the key is empty or blank, the method returns false.
     *
     * @param key      The key of the cache object on which the timeout should be set.
     * @param timeout  The expiration timeout for the cache object.
     * @param unit     The time unit for the expiration timeout.
     * @param callback An optional callback for handling exceptions, an instance of {@link Redis4jWrapCallback}.
     * @return true if the expiration timeout was successfully set; false otherwise.
     */
    public static boolean expire(String key, long timeout, TimeUnit unit, Redis4jWrapCallback callback) {
        Redis4jService e = jProvider();
        if (e == null) {
            return false;
        }
        return e.expire(dispatch(), key, timeout, unit, callback);
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
     * Retrieves a cache object from Redis using the given RedisTemplate, with an optional callback
     * for handling exceptions. If the dispatch template is null, or if the key is empty or blank,
     * the method returns null.
     *
     * @param key      The key of the cache object to retrieve.
     * @param callback An optional callback for handling exceptions, an instance of {@link Redis4jWrapCallback}.
     * @param <T>      The type of the value being retrieved.
     * @return The cached object associated with the given key, or null if the dispatch template is null or the key is empty/blank.
     */
    public static <T> T getCacheObject(String key, Redis4jWrapCallback callback) {
        Redis4jService e = jProvider();
        if (e == null) {
            return null;
        }
        return e.getCacheObject(dispatch(), key, callback);
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
     * Removes a cache object from Redis using the given RedisTemplate, with an optional callback
     * for handling exceptions. If the dispatch template is null, or if the key is empty or blank,
     * the method returns false.
     *
     * @param key      The key of the cache object to remove.
     * @param callback An optional callback for handling exceptions, an instance of {@link Redis4jWrapCallback}.
     * @return true if the cache object was successfully removed; false otherwise.
     */
    public static boolean removeObject(String key, Redis4jWrapCallback callback) {
        Redis4jService e = jProvider();
        if (e == null) {
            return false;
        }
        return e.removeObject(dispatch(), key, callback);
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
     * Stores a list of objects in Redis using the given RedisTemplate, with an optional callback
     * for handling exceptions. If the dispatch template is null, the list is empty, or the key is empty or blank,
     * the method returns 0.
     *
     * @param key      The key under which the list is stored.
     * @param list     The list of objects to store.
     * @param callback An optional callback for handling exceptions, an instance of {@link Redis4jWrapCallback}.
     * @param <T>      The type of objects in the list.
     * @return The number of elements in the list that were successfully stored; 0 if the operation failed or the inputs were invalid.
     */
    public static <T> long setCacheList(String key, List<T> list, Redis4jWrapCallback callback) {
        Redis4jService e = jProvider();
        if (e == null) {
            return -1;
        }
        return e.setCacheList(dispatch(), key, list, callback);
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
     * Retrieves a list of objects from Redis using the given RedisTemplate, with an optional callback
     * for handling exceptions. If the dispatch template is null, or if the key is empty or blank,
     * the method returns an empty list.
     *
     * @param key      The key under which the list is stored.
     * @param callback An optional callback for handling exceptions, an instance of {@link Redis4jWrapCallback}.
     * @param <T>      The type of objects in the list.
     * @return The list of objects stored under the given key; an empty list if the operation failed or the inputs were invalid.
     */
    public static <T> List<T> getCacheList(String key, Redis4jWrapCallback callback) {
        Redis4jService e = jProvider();
        if (e == null) {
            return Collections.emptyList();
        }
        return e.getCacheList(dispatch(), key, callback);
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
     * Stores a set of objects in Redis using the given RedisTemplate, with an optional callback
     * for handling exceptions. If the dispatch template is null, the dataSet is empty, or the key is empty or blank,
     * the method returns null.
     *
     * @param key      The key under which the set is stored.
     * @param dataSet  The set of data to be stored.
     * @param callback An optional callback for handling exceptions, an instance of {@link Redis4jWrapCallback}.
     * @param <T>      The type of objects in the set.
     * @return The BoundSetOperations for the given key and set, or null if the operation failed or the inputs were invalid.
     */
    public static <T> BoundSetOperations<String, T> setCacheSet(String key, Set<T> dataSet, Redis4jWrapCallback callback) {
        Redis4jService e = jProvider();
        if (e == null) {
            return null;
        }
        return e.setCacheSet(dispatch(), key, dataSet, callback);
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
     * Retrieves a set of objects from Redis using the given RedisTemplate and key, with an optional callback
     * for handling exceptions. If the dispatch template is null or the key is empty or blank, the method returns an empty set.
     *
     * @param key      The key under which the set is stored.
     * @param callback An optional callback for handling exceptions, an instance of {@link Redis4jWrapCallback}.
     * @param <T>      The type of objects in the set.
     * @return The set of objects retrieved from Redis, or an empty set if the operation failed or the inputs were invalid.
     */
    public static <T> Set<T> getCacheSet(String key, Redis4jWrapCallback callback) {
        Redis4jService e = jProvider();
        if (e == null) {
            return Collections.emptySet();
        }
        return e.getCacheSet(dispatch(), key, callback);
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
     * Stores a map of objects in Redis using the given RedisTemplate and key, with an optional callback
     * for handling exceptions. If the dispatch template is null, the map is empty, or the key is empty or blank,
     * the method does nothing.
     *
     * @param key      The key under which the map will be stored.
     * @param map      The map of objects to be stored in Redis.
     * @param callback An optional callback for handling exceptions, an instance of {@link Redis4jWrapCallback}.
     * @param <T>      The type of objects in the map.
     */
    public static <T> void setCacheMap(String key, Map<String, T> map, Redis4jWrapCallback callback) {
        Redis4jService e = jProvider();
        if (e == null) {
            return;
        }
        e.setCacheMap(dispatch(), key, map, callback);
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
     * Stores a map of objects in Redis using the given RedisTemplate and key, with an optional callback
     * for handling exceptions. This method allows for a variable number of key-value pairs to be provided as arguments.
     * If the dispatch template is null, the map is empty, or the key is empty or blank,
     * the method does nothing.
     *
     * @param key      The key under which the map will be stored.
     * @param callback An optional callback for handling exceptions, an instance of {@link Redis4jWrapCallback}.
     * @param map      A variable number of key-value pairs to be stored in Redis.
     * @param <T>      The type of objects in the map.
     */
    @SuppressWarnings({"unused", "unchecked"})
    public static <T> void setCacheMapSafe(String key, Redis4jWrapCallback callback, Pair<String, T>... map) {
        Redis4jService e = jProvider();
        if (e == null) {
            return;
        }
        e.setCacheMapSafe(dispatch(), key, callback, map);
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
     * Retrieves a map of objects from Redis using the given RedisTemplate and key, with an optional callback
     * for handling exceptions. If the dispatch template is null or the key is empty or blank,
     * the method returns an empty map.
     *
     * @param key      The key under which the map is stored.
     * @param callback An optional callback for handling exceptions, an instance of {@link Redis4jWrapCallback}.
     * @return A map of objects retrieved from Redis, or an empty map if the dispatch template or key is invalid.
     */
    public static Map<Object, Object> getCacheMap(String key, Redis4jWrapCallback callback) {
        Redis4jService e = jProvider();
        if (e == null) {
            return Collections.emptyMap();
        }
        return e.getCacheMap(dispatch(), key, callback);
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
     * Sets a value in a Redis hash using the given RedisTemplate, key, and hash key, with an optional callback
     * for handling exceptions. If the dispatch template is null, the value is null, or the key or hash key is empty or blank,
     * the method returns without performing any operation.
     *
     * @param key      The key under which the hash is stored.
     * @param hKey     The hash key under which the value is stored.
     * @param value    The value to be set in the hash.
     * @param callback An optional callback for handling exceptions, an instance of {@link Redis4jWrapCallback}.
     * @param <T>      The type of the value being set.
     */
    public static <T> void setCacheMapValue(String key, String hKey, T value, Redis4jWrapCallback callback) {
        Redis4jService e = jProvider();
        if (e == null) {
            return;
        }
        e.setCacheMapValue(dispatch(), key, hKey, value, callback);
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
     * Retrieves a value from a Redis hash using the given RedisTemplate, key, and hash key, with an optional callback
     * for handling exceptions. If the dispatch template is null, or if the key or hash key is empty or blank,
     * the method returns null.
     *
     * @param key      The key under which the hash is stored.
     * @param hKey     The hash key under which the value is stored.
     * @param callback An optional callback for handling exceptions, an instance of {@link Redis4jWrapCallback}.
     * @param <T>      The type of the value to be retrieved.
     * @return The value from the hash corresponding to the provided hash key, or null if not found or if inputs are invalid.
     */
    public static <T> T getCacheMapValue(String key, String hKey, Redis4jWrapCallback callback) {
        Redis4jService e = jProvider();
        if (e == null) {
            return null;
        }
        return e.getCacheMapValue(dispatch(), key, hKey, callback);
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

    /**
     * Retrieves multiple values from a Redis hash using the given RedisTemplate, key, and collection of hash keys, with an optional callback
     * for handling exceptions. If the dispatch template is null, or if the key or collection of hash keys is empty or blank,
     * the method returns an empty list.
     *
     * @param key      The key under which the hash is stored.
     * @param hKeys    The collection of hash keys for which values need to be retrieved.
     * @param callback An optional callback for handling exceptions, an instance of {@link Redis4jWrapCallback}.
     * @param <T>      The type of the values to be retrieved.
     * @return A list of values from the hash corresponding to the provided hash keys, or an empty list if inputs are invalid.
     */
    public static <T> List<T> getMultiCacheMapValue(String key, Collection<Object> hKeys, Redis4jWrapCallback callback) {
        Redis4jService e = jProvider();
        if (e == null) {
            return Collections.emptyList();
        }
        return e.getMultiCacheMapValue(dispatch(), key, hKeys, callback);
    }

    /**
     * Retrieves a collection of all keys from the Redis cache using the given RedisTemplate.
     * Uses a wildcard pattern to match all keys.
     *
     * @return A collection of all keys in the Redis cache, or an empty collection if the template is null.
     */
    public static Collection<String> defaultKeys() {
        Redis4jService e = jProvider();
        if (e == null) {
            return Collections.emptyList();
        }
        return e.defaultKeys(dispatch());
    }

    /**
     * Retrieves a collection of all keys from the Redis cache using the given RedisTemplate,
     * with an optional callback for handling exceptions. Uses a wildcard pattern to match all keys.
     *
     * @param callback An optional callback for handling exceptions, an instance of {@link Redis4jWrapCallback}.
     * @return A collection of all keys in the Redis cache, or an empty collection if an exception occurs.
     */
    public static Collection<String> defaultKeys(Redis4jWrapCallback callback) {
        Redis4jService e = jProvider();
        if (e == null) {
            return Collections.emptyList();
        }
        return e.defaultKeys(dispatch(), callback);
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
     * Checks if a specific key exists in the Redis store using the given RedisTemplate,
     * with an optional callback for handling exceptions.
     * If the dispatch template is null, or if the key is empty or blank, the method returns false.
     * Trims any whitespace from the key before checking its existence in the Redis store.
     *
     * @param key      The key to check for existence in the Redis store.
     * @param callback An optional callback for handling exceptions, an instance of {@link Redis4jWrapCallback}.
     * @return true if the key exists in the Redis store; false otherwise.
     */
    public static boolean containsKey(String key, Redis4jWrapCallback callback) {
        Redis4jService e = jProvider();
        if (e == null) {
            return false;
        }
        return e.containsKey(dispatch(), key, callback);
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
     * Publishes data to a specified Redis topic using the given RedisTemplate,
     * with an optional callback for handling exceptions.
     * If the dispatch template, topic, or data is null, the method returns without performing any action.
     * Attempts to send the data to the specified topic, and logs any exceptions that occur during the operation.
     *
     * @param topic    The Redis topic to which the data is to be sent.
     * @param data     The data to be sent to the topic.
     * @param callback An optional callback for handling exceptions, an instance of {@link Redis4jWrapCallback}.
     * @param <T>      The type of data being sent.
     */
    public static <T> void produce(ChannelTopic topic, T data, Redis4jWrapCallback callback) {
        Redis4jService e = jProvider();
        if (e == null) {
            return;
        }
        e.produce(dispatch(), topic, data, callback);
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
     * Increases the value of a numeric key in Redis, with an optional callback for handling exceptions.
     * If the dispatch template or key is null or empty, returns -1 indicating failure.
     * Uses Redis execute method to atomically increment the key value.
     * Logs any exceptions that occur during the operation.
     *
     * @param key      The key whose value is to be incremented.
     * @param callback An optional callback for handling exceptions, an instance of {@link Redis4jWrapCallback}.
     * @return The incremented value of the key, or -1 if an error occurs.
     */
    public static long increaseKey(String key, Redis4jWrapCallback callback) {
        Redis4jService e = jProvider();
        if (e == null) {
            return -1;
        }
        return e.increaseKey(dispatch(), key, callback);
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
     * Decreases the value of a numeric key in Redis, with an optional callback for handling exceptions.
     * If the dispatch template or key is null or empty, returns -1 indicating failure.
     * Uses Redis execute method to atomically decrement the key value.
     * Logs any exceptions that occur during the operation.
     *
     * @param key      The key whose value is to be decremented.
     * @param callback An optional callback for handling exceptions, an instance of {@link Redis4jWrapCallback}.
     * @return The decremented value of the key, or -1 if an error occurs.
     */
    public static long decreaseKey(String key, Redis4jWrapCallback callback) {
        Redis4jService e = jProvider();
        if (e == null) {
            return -1;
        }
        return e.decreaseKey(dispatch(), key, callback);
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
     * Increases the value of a numeric key in Redis by a specified increment, with an optional callback for handling exceptions.
     * If the dispatch template, key, or increment value is invalid (null, empty, negative), returns -1 indicating failure.
     * Uses Redis execute method with a callback to atomically increment the key value by the specified amount.
     * Logs any exceptions that occur during the operation.
     *
     * @param key      The key whose value is to be incremented.
     * @param value    The amount by which to increment the key's value.
     * @param callback An optional callback for handling exceptions, an instance of {@link Redis4jWrapCallback}.
     * @return The incremented value of the key, or -1 if an error occurs.
     */
    public static long increaseKeyBy(String key, long value, Redis4jWrapCallback callback) {
        Redis4jService e = jProvider();
        if (e == null) {
            return -1;
        }
        return e.increaseKeyBy(dispatch(), key, value, callback);
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
     * Decreases the value of a numeric key in Redis by a specified decrement, with an optional callback for handling exceptions.
     * If the dispatch template, key, or decrement value is invalid (null, empty, negative), returns -1 indicating failure.
     * Uses Redis execute method with a callback to atomically decrement the key value by the specified amount.
     * Logs any exceptions that occur during the operation.
     *
     * @param key      The key whose value is to be decremented.
     * @param value    The amount by which to decrement the key's value.
     * @param callback An optional callback for handling exceptions, an instance of {@link Redis4jWrapCallback}.
     * @return The decremented value of the key, or -1 if an error occurs.
     */
    public static long decreaseKeyBy(String key, long value, Redis4jWrapCallback callback) {
        Redis4jService e = jProvider();
        if (e == null) {
            return -1;
        }
        return e.decreaseKeyBy(dispatch(), key, value, callback);
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
     * Increases the value of a numeric key in Redis and sets an expiration time for the key, with an optional callback for handling exceptions.
     * If the dispatch template, key, timeout, or time unit is invalid (null, empty, negative), returns -1 indicating failure.
     * Uses the {@link #increaseKey} method to increment the key's value and then sets an expiration using the provided timeout and unit.
     * Logs any exceptions that occur during the operation.
     *
     * @param key      The key whose value is to be incremented and set with expiration.
     * @param timeout  The duration after which the key should expire.
     * @param unit     The time unit of the expiration timeout.
     * @param callback An optional callback for handling exceptions, an instance of {@link Redis4jWrapCallback}.
     * @return The incremented value of the key, or -1 if an error occurs.
     */
    public static long increaseKeyEx(String key, long timeout, TimeUnit unit, Redis4jWrapCallback callback) {
        Redis4jService e = jProvider();
        if (e == null) {
            return -1;
        }
        return e.increaseKeyEx(dispatch(), key, timeout, unit, callback);
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
     * Decreases the value of a numeric key in Redis and sets an expiration time for the key, with an optional callback for handling exceptions.
     * If the dispatch template, key, timeout, or time unit is invalid (null, empty, negative), returns -1 indicating failure.
     * Uses the {@link #decreaseKey} method to decrement the key's value and then sets an expiration using the provided timeout and unit.
     * Logs any exceptions that occur during the operation.
     *
     * @param key      The key whose value is to be decremented and set with expiration.
     * @param timeout  The duration after which the key should expire.
     * @param unit     The time unit of the expiration timeout.
     * @param callback An optional callback for handling exceptions, an instance of {@link Redis4jWrapCallback}.
     * @return The decremented value of the key, or -1 if an error occurs.
     */
    public static long decreaseKeyEx(String key, long timeout, TimeUnit unit, Redis4jWrapCallback callback) {
        Redis4jService e = jProvider();
        if (e == null) {
            return -1;
        }
        return e.decreaseKeyEx(dispatch(), key, timeout, unit, callback);
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
     * Increases the value of a numeric key in Redis by a specified increment and sets an expiration time for the key, with an optional callback for handling exceptions.
     * If the dispatch template, key, timeout, unit, or value is invalid (null, empty, negative), returns -1 indicating failure.
     * Uses the {@link #increaseKeyBy} method to increment the key's value by the specified amount and then sets an expiration using the provided timeout and unit.
     * Logs any exceptions that occur during the operation.
     *
     * @param key      The key whose value is to be incremented and set with expiration.
     * @param value    The amount by which to increment the key's value.
     * @param timeout  The duration after which the key should expire.
     * @param unit     The time unit of the expiration timeout.
     * @param callback An optional callback for handling exceptions, an instance of {@link Redis4jWrapCallback}.
     * @return The incremented value of the key, or -1 if an error occurs.
     */
    public static long increaseKeyByEx(String key, long value, long timeout, TimeUnit unit, Redis4jWrapCallback callback) {
        Redis4jService e = jProvider();
        if (e == null) {
            return -1;
        }
        return e.increaseKeyByEx(dispatch(), key, value, timeout, unit, callback);
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
     * Decreases the value of a numeric key in Redis by a specified decrement and sets an expiration time for the key, with an optional callback for handling exceptions.
     * If the dispatch template, key, timeout, unit, or value is invalid (null, empty, negative), returns -1 indicating failure.
     * Uses the {@link #decreaseKeyBy} method to decrement the key's value by the specified amount and then sets an expiration using the provided timeout and unit.
     * Logs any exceptions that occur during the operation.
     *
     * @param key      The key whose value is to be decremented and set with expiration.
     * @param value    The amount by which to decrement the key's value.
     * @param timeout  The duration after which the key should expire.
     * @param unit     The time unit of the expiration timeout.
     * @param callback An optional callback for handling exceptions, an instance of {@link Redis4jWrapCallback}.
     * @return The decremented value of the key, or -1 if an error occurs.
     */
    public static long decreaseKeyByEx(String key, long value, long timeout, TimeUnit unit, Redis4jWrapCallback callback) {
        Redis4jService e = jProvider();
        if (e == null) {
            return -1;
        }
        return e.decreaseKeyByEx(dispatch(), key, value, timeout, unit, callback);
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

    /**
     * Set a key-value pair in Redis.
     *
     * @param key   the key
     * @param value the value
     * @return the result of the set operation
     */
    public static String set(String key, String value) {
        return syncCommands().set(key, value);
    }

    /**
     * Get the value of a key from Redis.
     *
     * @param key the key
     * @return the value
     */
    public static String get(String key) {
        return syncCommands().get(key);
    }

    /**
     * Publish a message to a Redis channel.
     *
     * @param channel the channel
     * @param message the message
     * @return the number of clients that received the message
     */
    public static Long publish(String channel, String message) {
        return syncCommands().publish(channel, message);
    }

    /**
     * Set a key-value pair with an expiration time.
     *
     * @param key     the key
     * @param value   the value
     * @param seconds the expiration time in seconds
     * @return the result of the set operation
     */
    @SuppressWarnings({"SpellCheckingInspection"})
    public static String setex(String key, String value, long seconds) {
        return syncCommands().setex(key, seconds, value);
    }

    /**
     * Check if a key exists in Redis.
     *
     * @param key the key
     * @return true if the key exists, false otherwise
     */
    public static boolean exists(String key) {
        return syncCommands().exists(key) > 0;
    }

    /**
     * Increment the value of a key by one.
     *
     * @param key the key
     * @return the new value
     */
    @SuppressWarnings({"SpellCheckingInspection"})
    public static Long incr(String key) {
        return syncCommands().incr(key);
    }

    /**
     * Decrement the value of a key by one.
     *
     * @param key the key
     * @return the new value
     */
    @SuppressWarnings({"SpellCheckingInspection"})
    public static Long decr(String key) {
        return syncCommands().decr(key);
    }

    /**
     * Get the length of a list.
     *
     * @param key the key
     * @return the length of the list
     */
    @SuppressWarnings({"SpellCheckingInspection"})
    public static Long llen(String key) {
        return syncCommands().llen(key);
    }

    /**
     * Append a value to a list.
     *
     * @param key   the key
     * @param value the value
     * @return the length of the list after the append operation
     */
    @SuppressWarnings({"SpellCheckingInspection"})
    public static Long rpush(String key, String value) {
        return syncCommands().rpush(key, value);
    }

    /**
     * Prepend a value to a list.
     *
     * @param key   the key
     * @param value the value
     * @return the length of the list after the prepend operation
     */
    @SuppressWarnings({"SpellCheckingInspection"})
    public static Long lpush(String key, String value) {
        return syncCommands().lpush(key, value);
    }

    /**
     * Remove and get the first element in a list.
     *
     * @param key the key
     * @return the value of the first element, or null if the list is empty
     */
    @SuppressWarnings({"SpellCheckingInspection"})
    public static String lpop(String key) {
        return syncCommands().lpop(key);
    }

    /**
     * Remove and get the last element in a list.
     *
     * @param key the key
     * @return the value of the last element, or null if the list is empty
     */
    @SuppressWarnings({"SpellCheckingInspection"})
    public static String rpop(String key) {
        return syncCommands().rpop(key);
    }

    /**
     * Get a range of elements from a list.
     *
     * @param key   the key
     * @param start the start index
     * @param stop  the stop index
     * @return the list of elements
     */
    @SuppressWarnings({"SpellCheckingInspection"})
    public static List<String> lrange(String key, long start, long stop) {
        return syncCommands().lrange(key, start, stop);
    }

    /**
     * Set the value of an element in a list by its index.
     *
     * @param key   the key
     * @param index the index
     * @param value the value
     * @return the result of the set operation
     */
    @SuppressWarnings({"SpellCheckingInspection"})
    public static String lset(String key, long index, String value) {
        return syncCommands().lset(key, index, value);
    }

    /**
     * Remove elements from a list.
     *
     * @param key   the key
     * @param count the number of elements to remove
     * @param value the value to match
     * @return the number of removed elements
     */
    public static Long lrm(String key, long count, String value) {
        return syncCommands().lrem(key, count, value);
    }

    /**
     * Get the value of a hash field.
     *
     * @param key   the key
     * @param field the field
     * @return the value of the field, or null if the field does not exist
     */
    @SuppressWarnings({"SpellCheckingInspection"})
    public static String hget(String key, String field) {
        return syncCommands().hget(key, field);
    }

    /**
     * Set the value of a hash field.
     *
     * @param key   the key
     * @param field the field
     * @param value the value
     * @return the result of the set operation
     */
    @SuppressWarnings({"SpellCheckingInspection"})
    public static String hset(String key, String field, String value) {
        syncCommands().hset(key, field, value);
        return value;
    }

    /**
     * Get all fields and values in a hash.
     *
     * @param key the key
     * @return a map of fields and values
     */
    @SuppressWarnings({"SpellCheckingInspection"})
    public static Map<String, String> hgetAll(String key) {
        return syncCommands().hgetall(key);
    }

    /**
     * Delete one or more hash fields.
     *
     * @param key    the key
     * @param fields the fields
     * @return the number of fields that were removed
     */
    @SuppressWarnings({"SpellCheckingInspection"})
    public static Long hdel(String key, String... fields) {
        return syncCommands().hdel(key, fields);
    }

    /**
     * Check if a hash field exists.
     *
     * @param key   the key
     * @param field the field
     * @return true if the field exists, false otherwise
     */
    @SuppressWarnings({"SpellCheckingInspection"})
    public static boolean hexists(String key, String field) {
        return syncCommands().hexists(key, field);
    }

    /**
     * Increment the integer value of a hash field by the given number.
     *
     * @param key   the key
     * @param field the field
     * @param value the increment value
     * @return the new value
     */
    @SuppressWarnings({"SpellCheckingInspection"})
    public static Long hincrBy(String key, String field, long value) {
        return syncCommands().hincrby(key, field, value);
    }

    /**
     * Get the time to live for a key.
     *
     * @param key the key
     * @return the time to live in seconds, or -1 if the key does not have an expiration time
     */
    public static Long ttl(String key) {
        return syncCommands().ttl(key);
    }

    /**
     * Set a key's time to live in seconds.
     *
     * @param key     the key
     * @param seconds the expiration time in seconds
     * @return true if the timeout was set, false otherwise
     */
    public static boolean expire(String key, long seconds) {
        return syncCommands().expire(key, seconds);
    }

    /**
     * Set a key's time to live in milliseconds.
     *
     * @param key    the key
     * @param millis the expiration time in milliseconds
     * @return true if the timeout was set, false otherwise
     */
    @SuppressWarnings({"SpellCheckingInspection"})
    public static boolean pexpire(String key, long millis) {
        return syncCommands().pexpire(key, millis);
    }

    /**
     * Remove the expiration time from a key.
     *
     * @param key the key
     * @return true if the timeout was removed, false otherwise
     */
    public static boolean persist(String key) {
        return syncCommands().persist(key);
    }

    /**
     * Append a value to a key.
     *
     * @param key   the key
     * @param value the value to append
     * @return the length of the string after the append operation
     */
    public static Long append(String key, String value) {
        return syncCommands().append(key, value);
    }

    /**
     * Get the values of all the given keys.
     *
     * @param keys the keys
     * @return a list of values
     */
    @SuppressWarnings({"SpellCheckingInspection"})
    public static List<String> mget(String... keys) {
        return syncCommands().mget(keys).stream().map(kv -> kv.hasValue() ? kv.getValue() : null).collect(Collectors.toList());
    }

    /**
     * Set multiple keys to multiple values.
     *
     * @param map the map of keys and values
     * @return the result of the mset operation
     */
    @SuppressWarnings({"SpellCheckingInspection"})
    public static String mset(Map<String, String> map) {
        return syncCommands().mset(map);
    }

    /**
     * Get the length of a string value stored at key.
     *
     * @param key the key
     * @return the length of the string at key
     */
    public static Long strlen(String key) {
        return syncCommands().strlen(key);
    }

    /**
     * Get a substring of the string stored at a key.
     *
     * @param key   the key
     * @param start the start offset
     * @param end   the end offset
     * @return the substring
     */
    @SuppressWarnings({"SpellCheckingInspection"})
    public static String getrange(String key, long start, long end) {
        return syncCommands().getrange(key, start, end);
    }

    /**
     * Add a member to a set stored at key.
     *
     * @param key    the key
     * @param member the member to add
     * @return the number of elements that were added to the set
     */
    @SuppressWarnings({"SpellCheckingInspection"})
    public static Long sadd(String key, String member) {
        return syncCommands().sadd(key, member);
    }

    /**
     * Remove a member from a set stored at key.
     *
     * @param key    the key
     * @param member the member to remove
     * @return the number of elements that were removed from the set
     */
    @SuppressWarnings({"SpellCheckingInspection"})
    public static Long srem(String key, String member) {
        return syncCommands().srem(key, member);
    }

    /**
     * Get all the members in a set.
     *
     * @param key the key
     * @return the members of the set
     */
    @SuppressWarnings({"SpellCheckingInspection"})
    public static Set<String> smembers(String key) {
        return syncCommands().smembers(key);
    }

    /**
     * Add a member with a score to a sorted set stored at key.
     *
     * @param key    the key
     * @param score  the score
     * @param member the member to add
     * @return the number of elements that were added to the sorted set
     */
    @SuppressWarnings({"SpellCheckingInspection"})
    public static Long zadd(String key, double score, String member) {
        return syncCommands().zadd(key, score, member);
    }

    /**
     * Remove a member from a sorted set stored at key.
     *
     * @param key    the key
     * @param member the member to remove
     * @return the number of elements that were removed from the sorted set
     */
    @SuppressWarnings({"SpellCheckingInspection"})
    public static Long zrem(String key, String member) {
        return syncCommands().zrem(key, member);
    }

    /**
     * Retrieves a map of default keys along with their Redis data types.
     *
     * @return a map where keys are default Redis keys and values are their corresponding data types.
     */
    public static Map<String, String> defaultKeysWk() {
        Collection<String> keys = canDefaultKeys();
        if (Collection4j.isEmpty(keys)) {
            return Collections.emptyMap();
        }
        return keys.stream().collect(Collectors.toMap(key -> key, key -> syncCommands().type(key)));
    }

    /**
     * Retrieves data from Redis based on the type of the specified key.
     *
     * @param key the Redis key for which data is to be retrieved.
     * @return a wrapped HTTP response containing the retrieved data, or an error response if the key is empty,
     * Redis connection fails, or the key type is unsupported.
     */
    @SuppressWarnings({"SpellCheckingInspection", "EnhancedSwitchMigration"})
    public static WrapResponse<?> wget(String key) {
        if (String4j.isEmpty(key)) {
            return new HttpWrapBuilder<>().badRequest("key is required").requestId(getCurrentSessionId()).build();
        }
        RedisCommands<String, String> command = syncCommands();
        if (command == null) {
            return new HttpWrapBuilder<>().internalServerError("Redis connection failure").requestId(getCurrentSessionId()).build();
        }
        String type = command.type(key);
        switch (type) {
            case "string":
                return new HttpWrapBuilder<>().ok(command.get(key)).requestId(getCurrentSessionId()).customFields("redis_key_type_stored", "string").build();
            case "list":
                return new HttpWrapBuilder<>().ok(command.lrange(key, 0, -1)).requestId(getCurrentSessionId()).customFields("redis_key_type_stored", "list").build();
            case "hash":
                return new HttpWrapBuilder<>().ok(command.hgetall(key)).requestId(getCurrentSessionId()).customFields("redis_key_type_stored", "hash").build();
            case "set":
                return new HttpWrapBuilder<>().ok(command.smembers(key)).requestId(getCurrentSessionId()).customFields("redis_key_type_stored", "set").build();
            case "zset":
                return new HttpWrapBuilder<>().ok(command.zrange(key, 0, -1)).requestId(getCurrentSessionId()).customFields("redis_key_type_stored", "zset").build();
            default:
                return new HttpWrapBuilder<>().message(String.format("unsupported type: %s", type))
                        .statusCode(HttpStatusBuilder.UN_PROCESSABLE_ENTITY)
                        .customFields("redis_key_type_stored_unsupported", type)
                        .requestId(getCurrentSessionId())
                        .build();
        }
    }
}
