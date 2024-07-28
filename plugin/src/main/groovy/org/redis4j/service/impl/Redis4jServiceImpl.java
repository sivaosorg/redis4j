package org.redis4j.service.impl;

import org.jetbrains.annotations.NotNull;
import org.redis4j.common.Redis4j;
import org.redis4j.service.Redis4jConfigService;
import org.redis4j.service.Redis4jService;
import org.redis4j.service.Redis4jWrapCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.unify4j.common.*;
import org.unify4j.model.builder.HttpStatusBuilder;
import org.unify4j.model.builder.HttpWrapBuilder;
import org.unify4j.model.c.Pair;
import org.unify4j.model.enums.IconType;

import java.util.*;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"FieldCanBeLocal", "DuplcatedCode"})
@Service
public class Redis4jServiceImpl implements Redis4jService {
    protected static final Logger logger = LoggerFactory.getLogger(Redis4jServiceImpl.class);

    protected final Redis4jConfigService redis4jConfigService;

    @Autowired
    public Redis4jServiceImpl(Redis4jConfigService redis4jConfigService) {
        this.redis4jConfigService = redis4jConfigService;
    }

    /**
     * Retrieves a list of basic objects from the cache that match the specified pattern.
     *
     * @param dispatch the Redis template, an instance of {@link RedisTemplate}
     * @param pattern  the string prefix used to match keys
     * @return a collection of keys matching the specified pattern, or an empty list if the dispatch is null
     */
    @Override
    public Collection<String> keys(RedisTemplate<String, Object> dispatch, String pattern) {
        if (dispatch == null) {
            return Collections.emptyList();
        }
        return dispatch.keys(pattern);
    }

    /**
     * Retrieves a list of basic objects from the cache that match the specified pattern,
     * with an optional callback for handling exceptions.
     *
     * @param dispatch the Redis template, an instance of {@link RedisTemplate}
     * @param pattern  the string prefix used to match keys
     * @param callback an optional callback for handling exceptions, an instance of {@link Redis4jWrapCallback}
     * @return a collection of keys matching the specified pattern, or an empty list if an exception occurs
     */
    @Override
    public Collection<String> keys(RedisTemplate<String, Object> dispatch, String pattern, Redis4jWrapCallback callback) {
        HttpWrapBuilder<?> response = new HttpWrapBuilder<>().ok(null).requestId(Redis4j.getCurrentSessionId());
        Collection<String> keys = new ArrayList<>();
        try {
            keys = this.keys(dispatch, pattern);
        } catch (Exception e) {
            if (redis4jConfigService.isDebugging()) {
                logger.error("Redis4j, getting all keys got an exception: {} by pattern: {}", e.getMessage(), pattern, e);
            }
            response
                    .statusCode(HttpStatusBuilder.INTERNAL_SERVER_ERROR)
                    .message("getting all redis keys failed")
                    .debug("cause", e.getMessage())
                    .errors(e)
                    .customFields("redis_key_pattern", pattern);
        }
        if (callback != null) {
            callback.onCallback(response.build());
        }
        return keys;
    }

    /**
     * Sets a cache object in Redis using the given RedisTemplate.
     * If the dispatch template or value is null, or if the key is empty or blank, the method returns without performing any operation.
     *
     * @param dispatch The RedisTemplate used to set the cache object.
     * @param key      The key under which the value should be stored.
     * @param value    The value to be cached.
     * @param <T>      The type of the value being cached.
     */
    @Override
    public <T> void setCacheObject(RedisTemplate<String, Object> dispatch, String key, T value) {
        if (dispatch == null || value == null) {
            return;
        }
        if (String4j.isEmpty(key) || String4j.isBlank(key)) {
            return;
        }
        key = String4j.trimWhitespace(key);
        dispatch.opsForValue().set(key, value);
        if (redis4jConfigService.isDebugging()) {
            logger.info("{} Setting Redis key: '{}', value: {}", IconType.DEBUG.getCode(), key, Class4j.isPrimitive(value.getClass()) ? value.toString() : Json4j.toJson(value));
        }
    }

    /**
     * Sets a cache object in Redis using the given RedisTemplate, with an optional callback
     * for handling exceptions. If the dispatch template or value is null, or if the key is empty
     * or blank, the method returns without performing any operation.
     *
     * @param dispatch The RedisTemplate used to set the cache object.
     * @param key      The key under which the value should be stored.
     * @param value    The value to be cached.
     * @param callback An optional callback for handling exceptions, an instance of {@link Redis4jWrapCallback}.
     * @param <T>      The type of the value being cached.
     */
    @Override
    public <T> void setCacheObject(RedisTemplate<String, Object> dispatch, String key, T value, Redis4jWrapCallback callback) {
        HttpWrapBuilder<?> response = new HttpWrapBuilder<>().ok(null).requestId(Redis4j.getCurrentSessionId());
        try {
            this.setCacheObject(dispatch, key, value);
        } catch (Exception e) {
            response
                    .statusCode(HttpStatusBuilder.INTERNAL_SERVER_ERROR)
                    .message("setting redis key failed")
                    .debug("cause", e.getMessage())
                    .errors(e)
                    .customFields("redis_key", key)
                    .customFields("redis_value", value);
        }
        if (callback != null) {
            callback.onCallback(response.build());
        }
    }

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
    @Override
    public <T> void setCacheObject(RedisTemplate<String, Object> dispatch, String key, T value, long timeout, TimeUnit unit) {
        if (dispatch == null || value == null || timeout < 0 || unit == null) {
            return;
        }
        if (String4j.isEmpty(key) || String4j.isBlank(key)) {
            return;
        }
        key = String4j.trimWhitespace(key);
        dispatch.opsForValue().set(key, value, timeout, unit);
        if (redis4jConfigService.isDebugging()) {
            logger.info("{} Setting Redis key: '{}', value: {} with timeout: {} ({})", IconType.DEBUG.getCode(), key, Class4j.isPrimitive(value.getClass()) ? value.toString() : Json4j.toJson(value), timeout, unit.toString());
        }
    }

    /**
     * Sets a cache object in Redis with an expiration timeout using the given RedisTemplate, with an optional callback
     * for handling exceptions. If the dispatch template, value, or time unit is null, or if the timeout is negative,
     * or if the key is empty or blank, the method returns without performing any operation.
     *
     * @param dispatch The RedisTemplate used to set the cache object.
     * @param key      The key under which the value should be stored.
     * @param value    The value to be cached.
     * @param timeout  The expiration timeout for the cached object.
     * @param unit     The time unit for the expiration timeout.
     * @param callback An optional callback for handling exceptions, an instance of {@link Redis4jWrapCallback}.
     * @param <T>      The type of the value being cached.
     */
    @Override
    public <T> void setCacheObject(RedisTemplate<String, Object> dispatch, String key, T value, long timeout, TimeUnit unit, Redis4jWrapCallback callback) {
        HttpWrapBuilder<?> response = new HttpWrapBuilder<>().ok(null).requestId(Redis4j.getCurrentSessionId());
        try {
            this.setCacheObject(dispatch, key, value, timeout, unit);
        } catch (Exception e) {
            response
                    .statusCode(HttpStatusBuilder.INTERNAL_SERVER_ERROR)
                    .message("setting redis key failed")
                    .debug("cause", e.getMessage())
                    .errors(e)
                    .customFields("redis_key", key)
                    .customFields("redis_value", value)
                    .customFields("redis_timeout", timeout);
        }
        if (callback != null) {
            callback.onCallback(response.build());
        }
    }

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
    @Override
    public boolean expire(RedisTemplate<String, Object> dispatch, String key, long timeout, TimeUnit unit) {
        if (dispatch == null || unit == null || timeout < 0) {
            return false;
        }
        if (String4j.isEmpty(key) || String4j.isBlank(key)) {
            return false;
        }
        key = String4j.trimWhitespace(key);
        if (redis4jConfigService.isDebugging()) {
            logger.info("{} Setting expiration for Redis key: '{}' by timeout: {}({})", IconType.DEBUG.getCode(), key, timeout, unit.toString());
        }
        return Boolean.TRUE.equals(dispatch.expire(key, timeout, unit));
    }

    /**
     * Sets an expiration timeout on a cache object in Redis using the given RedisTemplate, with an optional callback
     * for handling exceptions. If the dispatch template, time unit is null, or if the timeout is negative,
     * or if the key is empty or blank, the method returns false.
     *
     * @param dispatch The RedisTemplate used to set the expiration timeout.
     * @param key      The key of the cache object on which the timeout should be set.
     * @param timeout  The expiration timeout for the cache object.
     * @param unit     The time unit for the expiration timeout.
     * @param callback An optional callback for handling exceptions, an instance of {@link Redis4jWrapCallback}.
     * @return true if the expiration timeout was successfully set; false otherwise.
     */
    @Override
    public boolean expire(RedisTemplate<String, Object> dispatch, String key, long timeout, TimeUnit unit, Redis4jWrapCallback callback) {
        HttpWrapBuilder<?> response = new HttpWrapBuilder<>().ok(null).requestId(Redis4j.getCurrentSessionId());
        boolean isExpired = false;
        try {
            isExpired = this.expire(dispatch, key, timeout, unit);
        } catch (Exception e) {
            response
                    .statusCode(HttpStatusBuilder.INTERNAL_SERVER_ERROR)
                    .message("setting redis key expiration failed")
                    .debug("cause", e.getMessage())
                    .errors(e)
                    .customFields("redis_key", key)
                    .customFields("redis_timeout", timeout);
        }
        if (callback != null) {
            callback.onCallback(response.build());
        }
        return isExpired;
    }

    /**
     * Retrieves a cache object from Redis using the given RedisTemplate.
     * If the dispatch template is null, or if the key is empty or blank, the method returns null.
     *
     * @param dispatch The RedisTemplate used to retrieve the cache object.
     * @param key      The key of the cache object to retrieve.
     * @param <T>      The type of the value being retrieved.
     * @return The cached object associated with the given key, or null if the dispatch template is null or the key is empty/blank.
     */
    @SuppressWarnings({"unchecked"})
    @Override
    public <T> T getCacheObject(RedisTemplate<String, Object> dispatch, String key) {
        if (dispatch == null) {
            return null;
        }
        if (String4j.isEmpty(key) || String4j.isBlank(key)) {
            return null;
        }
        if (redis4jConfigService.isDebugging()) {
            logger.info("{} Getting Redis key: '{}'", IconType.DEBUG.getCode(), key);
        }
        ValueOperations<String, Object> operation = dispatch.opsForValue();
        return (T) operation.get(key);
    }

    /**
     * Retrieves a cache object from Redis using the given RedisTemplate, with an optional callback
     * for handling exceptions. If the dispatch template is null, or if the key is empty or blank,
     * the method returns null.
     *
     * @param dispatch The RedisTemplate used to retrieve the cache object.
     * @param key      The key of the cache object to retrieve.
     * @param callback An optional callback for handling exceptions, an instance of {@link Redis4jWrapCallback}.
     * @param <T>      The type of the value being retrieved.
     * @return The cached object associated with the given key, or null if the dispatch template is null or the key is empty/blank.
     */
    @Override
    public <T> T getCacheObject(RedisTemplate<String, Object> dispatch, String key, Redis4jWrapCallback callback) {
        HttpWrapBuilder<?> response = new HttpWrapBuilder<>().ok(null).requestId(Redis4j.getCurrentSessionId());
        T data = null;
        try {
            data = this.getCacheObject(dispatch, key);
        } catch (Exception e) {
            response
                    .statusCode(HttpStatusBuilder.INTERNAL_SERVER_ERROR)
                    .message("getting redis value failed")
                    .debug("cause", e.getMessage())
                    .errors(e)
                    .customFields("redis_key", key);
        }
        if (callback != null) {
            callback.onCallback(response.build());
        }
        return data;
    }

    /**
     * Removes a cache object from Redis using the given RedisTemplate.
     * If the dispatch template is null, or if the key is empty or blank, the method returns false.
     *
     * @param dispatch The RedisTemplate used to remove the cache object.
     * @param key      The key of the cache object to remove.
     * @return true if the cache object was successfully removed; false otherwise.
     */
    @Override
    public boolean removeObject(RedisTemplate<String, Object> dispatch, String key) {
        if (dispatch == null) {
            return false;
        }
        if (String4j.isEmpty(key) || String4j.isBlank(key)) {
            return false;
        }
        key = String4j.trimWhitespace(key);
        if (redis4jConfigService.isDebugging()) {
            logger.info("{} Removing Redis key: '{}'", IconType.DEBUG.getCode(), key);
        }
        return Boolean.TRUE.equals(dispatch.delete(key));
    }

    /**
     * Removes a cache object from Redis using the given RedisTemplate, with an optional callback
     * for handling exceptions. If the dispatch template is null, or if the key is empty or blank,
     * the method returns false.
     *
     * @param dispatch The RedisTemplate used to remove the cache object.
     * @param key      The key of the cache object to remove.
     * @param callback An optional callback for handling exceptions, an instance of {@link Redis4jWrapCallback}.
     * @return true if the cache object was successfully removed; false otherwise.
     */
    @Override
    public boolean removeObject(RedisTemplate<String, Object> dispatch, String key, Redis4jWrapCallback callback) {
        HttpWrapBuilder<?> response = new HttpWrapBuilder<>().ok(null).requestId(Redis4j.getCurrentSessionId());
        boolean isRemoved = false;
        try {
            isRemoved = this.removeObject(dispatch, key);
        } catch (Exception e) {
            response
                    .statusCode(HttpStatusBuilder.INTERNAL_SERVER_ERROR)
                    .message("removing redis key failed")
                    .debug("cause", e.getMessage())
                    .errors(e)
                    .customFields("redis_key", key);
        }
        if (callback != null) {
            callback.onCallback(response.build());
        }
        return isRemoved;
    }

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
    @Override
    public <T> long setCacheList(RedisTemplate<String, Object> dispatch, String key, List<T> list) {
        if (dispatch == null || Collection4j.isEmpty(list)) {
            return 0;
        }
        if (String4j.isEmpty(key) || String4j.isBlank(key)) {
            return 0;
        }
        key = String4j.trimWhitespace(key);
        Long count = dispatch.opsForList().rightPushAll(key, list);
        if (redis4jConfigService.isDebugging()) {
            logger.info("{} Setting list by Redis key: '{}', value: {}", IconType.DEBUG.getCode(), key, Class4j.isPrimitive(list.getClass()) ? list.toString() : Json4j.toJson(list));
        }
        return count == null ? 0 : count;
    }

    /**
     * Stores a list of objects in Redis using the given RedisTemplate, with an optional callback
     * for handling exceptions. If the dispatch template is null, the list is empty, or the key is empty or blank,
     * the method returns 0.
     *
     * @param dispatch The RedisTemplate used to store the list.
     * @param key      The key under which the list is stored.
     * @param list     The list of objects to store.
     * @param callback An optional callback for handling exceptions, an instance of {@link Redis4jWrapCallback}.
     * @param <T>      The type of objects in the list.
     * @return The number of elements in the list that were successfully stored; 0 if the operation failed or the inputs were invalid.
     */
    @Override
    public <T> long setCacheList(RedisTemplate<String, Object> dispatch, String key, List<T> list, Redis4jWrapCallback callback) {
        HttpWrapBuilder<?> response = new HttpWrapBuilder<>().ok(null).requestId(Redis4j.getCurrentSessionId());
        long affected = 0;
        try {
            affected = this.setCacheList(dispatch, key, list);
        } catch (Exception e) {
            response
                    .statusCode(HttpStatusBuilder.INTERNAL_SERVER_ERROR)
                    .message("setting redis key list failed")
                    .debug("cause", e.getMessage())
                    .errors(e)
                    .customFields("redis_key", key);
        }
        if (callback != null) {
            callback.onCallback(response.build());
        }
        return affected;
    }

    /**
     * Retrieves a list of objects from Redis using the given RedisTemplate.
     * If the dispatch template is null, or if the key is empty or blank, the method returns an empty list.
     *
     * @param dispatch The RedisTemplate used to retrieve the list.
     * @param key      The key under which the list is stored.
     * @param <T>      The type of objects in the list.
     * @return The list of objects stored under the given key; an empty list if the operation failed or the inputs were invalid.
     */
    @SuppressWarnings({"unchecked"})
    @Override
    public <T> List<T> getCacheList(RedisTemplate<String, Object> dispatch, String key) {
        if (dispatch == null) {
            return Collections.emptyList();
        }
        if (String4j.isEmpty(key) || String4j.isBlank(key)) {
            return Collections.emptyList();
        }
        key = String4j.trimWhitespace(key);
        return (List<T>) dispatch.opsForList().range(key, 0, -1);
    }

    /**
     * Retrieves a list of objects from Redis using the given RedisTemplate, with an optional callback
     * for handling exceptions. If the dispatch template is null, or if the key is empty or blank,
     * the method returns an empty list.
     *
     * @param dispatch The RedisTemplate used to retrieve the list.
     * @param key      The key under which the list is stored.
     * @param callback An optional callback for handling exceptions, an instance of {@link Redis4jWrapCallback}.
     * @param <T>      The type of objects in the list.
     * @return The list of objects stored under the given key; an empty list if the operation failed or the inputs were invalid.
     */
    @Override
    public <T> List<T> getCacheList(RedisTemplate<String, Object> dispatch, String key, Redis4jWrapCallback callback) {
        HttpWrapBuilder<?> response = new HttpWrapBuilder<>().ok(null).requestId(Redis4j.getCurrentSessionId());
        List<T> list = new ArrayList<>();
        try {
            list = this.getCacheList(dispatch, key);
        } catch (Exception e) {
            response
                    .statusCode(HttpStatusBuilder.INTERNAL_SERVER_ERROR)
                    .message("getting redis key list failed")
                    .debug("cause", e.getMessage())
                    .errors(e)
                    .customFields("redis_key", key);
        }
        if (callback != null) {
            callback.onCallback(response.build());
        }
        return list;
    }

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
    @SuppressWarnings({"unchecked"})
    @Override
    public <T> BoundSetOperations<String, T> setCacheSet(RedisTemplate<String, Object> dispatch, String key, Set<T> dataSet) {
        if (dispatch == null || Collection4j.isEmpty(dataSet)) {
            return null;
        }
        if (String4j.isEmpty(key) || String4j.isBlank(key)) {
            return null;
        }
        key = String4j.trimWhitespace(key);
        BoundSetOperations<String, Object> ops = dispatch.boundSetOps(key);
        Iterator<T> iterator = dataSet.iterator();
        if (iterator.hasNext()) {
            do {
                ops.add(iterator.next());
            } while (iterator.hasNext());
        }
        if (redis4jConfigService.isDebugging()) {
            logger.info("{} Setting Set by Redis key: '{}', value: {}", IconType.DEBUG.getCode(), key,
                    Class4j.isPrimitive(dataSet.getClass()) ? dataSet.toString() : Json4j.toJson(dataSet));
        }
        return (BoundSetOperations<String, T>) ops;
    }

    /**
     * Stores a set of objects in Redis using the given RedisTemplate, with an optional callback
     * for handling exceptions. If the dispatch template is null, the dataSet is empty, or the key is empty or blank,
     * the method returns null.
     *
     * @param dispatch The RedisTemplate used to store the set.
     * @param key      The key under which the set is stored.
     * @param dataSet  The set of data to be stored.
     * @param callback An optional callback for handling exceptions, an instance of {@link Redis4jWrapCallback}.
     * @param <T>      The type of objects in the set.
     * @return The BoundSetOperations for the given key and set, or null if the operation failed or the inputs were invalid.
     */
    @Override
    public <T> BoundSetOperations<String, T> setCacheSet(RedisTemplate<String, Object> dispatch, String key, Set<T> dataSet, Redis4jWrapCallback callback) {
        HttpWrapBuilder<?> response = new HttpWrapBuilder<>().ok(null).requestId(Redis4j.getCurrentSessionId());
        BoundSetOperations<String, T> data = null;
        try {
            data = this.setCacheSet(dispatch, key, dataSet);
        } catch (Exception e) {
            response
                    .statusCode(HttpStatusBuilder.INTERNAL_SERVER_ERROR)
                    .message("setting redis key set failed")
                    .debug("cause", e.getMessage())
                    .errors(e)
                    .customFields("redis_key", key);
        }
        if (callback != null) {
            callback.onCallback(response.build());
        }
        return data;
    }

    /**
     * Retrieves a set of objects from Redis using the given RedisTemplate and key.
     * If the dispatch template is null or the key is empty or blank, the method returns an empty set.
     *
     * @param dispatch The RedisTemplate used to retrieve the set.
     * @param key      The key under which the set is stored.
     * @param <T>      The type of objects in the set.
     * @return The set of objects retrieved from Redis, or an empty set if the operation failed or the inputs were invalid.
     */
    @SuppressWarnings({"unchecked"})
    @Override
    public <T> Set<T> getCacheSet(RedisTemplate<String, Object> dispatch, String key) {
        if (dispatch == null) {
            return Collections.emptySet();
        }
        if (String4j.isEmpty(key) || String4j.isBlank(key)) {
            return Collections.emptySet();
        }
        key = String4j.trimWhitespace(key);
        return (Set<T>) dispatch.opsForSet().members(key);
    }

    /**
     * Retrieves a set of objects from Redis using the given RedisTemplate and key, with an optional callback
     * for handling exceptions. If the dispatch template is null or the key is empty or blank, the method returns an empty set.
     *
     * @param dispatch The RedisTemplate used to retrieve the set.
     * @param key      The key under which the set is stored.
     * @param callback An optional callback for handling exceptions, an instance of {@link Redis4jWrapCallback}.
     * @param <T>      The type of objects in the set.
     * @return The set of objects retrieved from Redis, or an empty set if the operation failed or the inputs were invalid.
     */
    @Override
    public <T> Set<T> getCacheSet(RedisTemplate<String, Object> dispatch, String key, Redis4jWrapCallback callback) {
        HttpWrapBuilder<?> response = new HttpWrapBuilder<>().ok(null).requestId(Redis4j.getCurrentSessionId());
        Set<T> set = new HashSet<>();
        try {
            set = this.getCacheSet(dispatch, key);
        } catch (Exception e) {
            response
                    .statusCode(HttpStatusBuilder.INTERNAL_SERVER_ERROR)
                    .message("getting redis key set failed")
                    .debug("cause", e.getMessage())
                    .errors(e)
                    .customFields("redis_key", key);
        }
        if (callback != null) {
            callback.onCallback(response.build());
        }
        return set;
    }

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
    @Override
    public <T> void setCacheMap(RedisTemplate<String, Object> dispatch, String key, Map<String, T> map) {
        if (dispatch == null || Collection4j.isEmptyMap(map)) {
            return;
        }
        if (String4j.isEmpty(key) || String4j.isBlank(key)) {
            return;
        }
        key = String4j.trimWhitespace(key);
        dispatch.opsForHash().putAll(key, map);
        if (redis4jConfigService.isDebugging()) {
            logger.info("{} Setting Map by Redis key: '{}', value: {}", IconType.DEBUG.getCode(), key,
                    Class4j.isPrimitive(map.getClass()) ? map.toString() : Json4j.toJson(map));
        }
    }

    /**
     * Stores a map of objects in Redis using the given RedisTemplate and key.
     * If the dispatch template is null, the map is empty, or the key is empty or blank,
     * the method does nothing.
     *
     * @param dispatch The RedisTemplate used to store the map.
     * @param key      The key under which the map will be stored.
     * @param map      The map of objects to be stored in Redis.
     */
    @SafeVarargs
    @Override
    public final <T> void setCacheMapSafe(RedisTemplate<String, Object> dispatch, String key, Pair<String, T>... map) {
        if (dispatch == null || map == null || Object4j.isEmpty(map)) {
            return;
        }
        this.setCacheMap(dispatch, key, Collection4j.mapOf(map));
    }

    /**
     * Retrieves a map of objects from Redis using the given RedisTemplate and key.
     * If the dispatch template is null or the key is empty or blank,
     * the method returns an empty map.
     *
     * @param dispatch The RedisTemplate used to retrieve the map.
     * @param key      The key under which the map is stored.
     * @return A map of objects retrieved from Redis, or an empty map if the dispatch template or key is invalid.
     */
    @Override
    public Map<Object, Object> getCacheMap(RedisTemplate<String, Object> dispatch, String key) {
        if (dispatch == null) {
            return Collections.emptyMap();
        }
        if (String4j.isEmpty(key) || String4j.isBlank(key)) {
            return Collections.emptyMap();
        }
        key = String4j.trimWhitespace(key);
        return dispatch.opsForHash().entries(key);
    }

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
    @Override
    public <T> void setCacheMapValue(RedisTemplate<String, Object> dispatch, String key, String hKey, T value) {
        if (dispatch == null || value == null) {
            return;
        }
        if (String4j.isEmpty(key) || String4j.isBlank(key)) {
            return;
        }
        if (String4j.isEmpty(hKey) || String4j.isBlank(hKey)) {
            return;
        }
        key = String4j.trimWhitespace(key);
        dispatch.opsForHash().put(key, hKey, value);
        if (redis4jConfigService.isDebugging()) {
            logger.info("{} Setting Map-Value by Redis key: '{}', value: {}", IconType.DEBUG.getCode(), key,
                    Class4j.isPrimitive(value.getClass()) ? value.toString() : Json4j.toJson(value));
        }
    }

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
    @Override
    public <T> T getCacheMapValue(RedisTemplate<String, Object> dispatch, String key, String hKey) {
        if (dispatch == null) {
            return null;
        }
        if (String4j.isEmpty(key) || String4j.isBlank(key)) {
            return null;
        }
        if (String4j.isEmpty(hKey) || String4j.isBlank(hKey)) {
            return null;
        }
        key = String4j.trimWhitespace(key);
        HashOperations<String, String, T> ops = dispatch.opsForHash();
        return ops.get(key, hKey);
    }

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
    @SuppressWarnings({"unchecked"})
    @Override
    public <T> List<T> getMultiCacheMapValue(RedisTemplate<String, Object> dispatch, String key, Collection<Object> hKeys) {
        if (dispatch == null || Collection4j.isEmpty(hKeys)) {
            return Collections.emptyList();
        }
        if (String4j.isEmpty(key) || String4j.isBlank(key)) {
            return Collections.emptyList();
        }
        key = String4j.trimWhitespace(key);
        return (List<T>) dispatch.opsForHash().multiGet(key, hKeys);
    }

    /**
     * Get list of basic objects of cache
     *
     * @param dispatch the Redis template, class {@link RedisTemplate}
     * @return object list
     */
    @Override
    public Collection<String> defaultKeys(RedisTemplate<String, Object> dispatch) {
        return keys(dispatch, "*");
    }

    /**
     * Checks if a specific key exists in the Redis store using the given RedisTemplate.
     * If the dispatch template is null, or if the key is empty or blank, the method returns false.
     * Trims any whitespace from the key before checking its existence in the Redis store.
     *
     * @param dispatch The RedisTemplate used to check the existence of the key.
     * @param key      The key to check for existence in the Redis store.
     * @return true if the key exists in the Redis store; false otherwise.
     */
    @Override
    public boolean containsKey(RedisTemplate<String, Object> dispatch, String key) {
        if (dispatch == null) {
            return false;
        }
        if (String4j.isEmpty(key) || String4j.isBlank(key)) {
            return false;
        }
        key = String4j.trimWhitespace(key);
        Collection<String> collection = this.defaultKeys(dispatch);
        return Collection4j.isNotEmpty(collection) && collection.contains(key);
    }

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
    @Override
    public <T> void produce(RedisTemplate<String, Object> dispatch, ChannelTopic topic, T data) {
        if (dispatch == null || topic == null || data == null) {
            return;
        }
        try {
            dispatch.convertAndSend(topic.getTopic(), data);
            if (redis4jConfigService.isDebugging()) {
                logger.info("{} Redis4j, producing data to topic '{}' by data: {}",
                        IconType.DEBUG.getCode(), topic.getTopic(),
                        Class4j.isPrimitive(data.getClass()) ? data.toString() : Json4j.toJson(data));
            }
        } catch (Exception e) {
            logger.error("{} Redis4j, producing data to topic '{}' got an exception: {}", IconType.ERROR.getCode(), topic.getTopic(), e.getMessage(), e);
        }
    }

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
    @SuppressWarnings({"unchecked", "rawtypes", "DataFlowIssue"})
    @Override
    public long increaseKey(RedisTemplate<String, Object> dispatch, String key) {
        if (dispatch == null) {
            return -1;
        }
        if (String4j.isEmpty(key) || String4j.isBlank(key)) {
            return -1;
        }
        try {
            return (long) dispatch.execute((RedisCallback) connection -> {
                byte[] b = dispatch.getStringSerializer().serialize(key);
                return connection.incr(b);
            }, true);
        } catch (Exception e) {
            logger.error("{} Redis4j, increasing key '{}' got an exception: {}", IconType.ERROR.getCode(), key, e.getMessage(), e);
            return -1;
        }
    }

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
    @SuppressWarnings({"unchecked", "rawtypes", "DataFlowIssue"})
    @Override
    public long decreaseKey(RedisTemplate<String, Object> dispatch, String key) {
        if (dispatch == null) {
            return -1;
        }
        if (String4j.isEmpty(key) || String4j.isBlank(key)) {
            return -1;
        }
        try {
            return (long) dispatch.execute((RedisCallback) connection -> {
                byte[] b = dispatch.getStringSerializer().serialize(key);
                return connection.decr(b);
            }, true);
        } catch (Exception e) {
            logger.error("{} Redis4j, decreasing key '{}' got an exception: {}", IconType.ERROR.getCode(), key, e.getMessage(), e);
            return -1;
        }
    }

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
    @SuppressWarnings({"unchecked", "rawtypes", "DataFlowIssue"})
    @Override
    public long increaseKeyBy(RedisTemplate<String, Object> dispatch, String key, long value) {
        if (dispatch == null || value < 0) {
            return -1;
        }
        if (String4j.isEmpty(key) || String4j.isBlank(key)) {
            return -1;
        }
        try {
            final String preKey = key;
            final long preValue = value;
            return (long) dispatch.execute(new RedisCallback() {
                public Object doInRedis(@NotNull RedisConnection connection) {
                    byte[] b = dispatch.getStringSerializer().serialize(preKey);
                    return connection.incrBy(b, preValue);
                }
            }, true);
        } catch (Exception e) {
            logger.error("{} Redis4j, increasing key '{}' got an exception: {}", IconType.ERROR.getCode(), key, e.getMessage(), e);
            return -1;
        }
    }

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
    @SuppressWarnings({"unchecked", "rawtypes", "DataFlowIssue"})
    @Override
    public long decreaseKeyBy(RedisTemplate<String, Object> dispatch, String key, long value) {
        if (dispatch == null || value < 0) {
            return -1;
        }
        if (String4j.isEmpty(key) || String4j.isBlank(key)) {
            return -1;
        }
        try {
            final String preKey = key;
            final long preValue = value;
            return (long) dispatch.execute(new RedisCallback() {
                public Object doInRedis(@NotNull RedisConnection connection) {
                    byte[] b = dispatch.getStringSerializer().serialize(preKey);
                    return connection.decrBy(b, preValue);
                }
            }, true);
        } catch (Exception e) {
            logger.error("{} Redis4j, decreasing key '{}' got an exception: {}", IconType.ERROR.getCode(), key, e.getMessage(), e);
            return -1;
        }
    }

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
    @Override
    public long increaseKeyEx(RedisTemplate<String, Object> dispatch, String key, long timeout, TimeUnit unit) {
        if (dispatch == null || timeout < 0 || unit == null) {
            return -1;
        }
        if (String4j.isEmpty(key) || String4j.isBlank(key)) {
            return -1;
        }
        long value = this.increaseKey(dispatch, key);
        dispatch.expire(key, timeout, unit);
        return value;
    }

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
    @Override
    public long decreaseKeyEx(RedisTemplate<String, Object> dispatch, String key, long timeout, TimeUnit unit) {
        if (dispatch == null || timeout < 0 || unit == null) {
            return -1;
        }
        if (String4j.isEmpty(key) || String4j.isBlank(key)) {
            return -1;
        }
        long value = this.decreaseKey(dispatch, key);
        dispatch.expire(key, timeout, unit);
        return value;
    }

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
    @Override
    public long increaseKeyByEx(RedisTemplate<String, Object> dispatch, String key, long value, long timeout, TimeUnit unit) {
        if (dispatch == null || unit == null || timeout < 0 || value < 0) {
            return -1;
        }
        if (String4j.isEmpty(key) || String4j.isBlank(key)) {
            return -1;
        }
        long _value = this.increaseKeyBy(dispatch, key, value);
        dispatch.expire(key, timeout, unit);
        return _value;
    }

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
    @Override
    public long decreaseKeyByEx(RedisTemplate<String, Object> dispatch, String key, long value, long timeout, TimeUnit unit) {
        if (dispatch == null || unit == null || timeout < 0 || value < 0) {
            return -1;
        }
        if (String4j.isEmpty(key) || String4j.isBlank(key)) {
            return -1;
        }
        long _value = this.decreaseKeyBy(dispatch, key, value);
        dispatch.expire(key, timeout, unit);
        return _value;
    }
}
