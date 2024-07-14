package org.redis4j.service.impl;

import org.redis4j.service.Redis4jConfigService;
import org.redis4j.service.Redis4jService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.unify4j.common.Class4j;
import org.unify4j.common.Json4j;
import org.unify4j.common.String4j;
import org.unify4j.model.enums.IconType;

import java.util.Collection;
import java.util.Collections;
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
     * Get list of basic objects of cache
     *
     * @param dispatch - the Redis template, class {@link RedisTemplate}
     * @param pattern  string prefix
     * @return object list
     */
    @Override
    public Collection<String> keys(RedisTemplate<String, Object> dispatch, String pattern) {
        if (dispatch == null) {
            return Collections.emptyList();
        }
        return dispatch.keys(pattern);
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
        return Boolean.TRUE.equals(dispatch.expire(key, timeout, unit));
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
        ValueOperations<String, Object> operation = dispatch.opsForValue();
        return (T) operation.get(key);
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
        return Boolean.TRUE.equals(dispatch.delete(key));
    }
}
