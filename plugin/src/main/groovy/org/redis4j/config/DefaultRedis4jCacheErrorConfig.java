package org.redis4j.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.stereotype.Component;

@SuppressWarnings({"NullableProblems"})
@Component
public class DefaultRedis4jCacheErrorConfig implements CacheErrorHandler {
    protected static final Logger logger = LoggerFactory.getLogger(DefaultRedis4jCacheErrorConfig.class);

    @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        logger.error("Redis4j, cache get error for key: {} in cache: {}", key, cache.getName(), exception);
    }

    @Override
    public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
        logger.error("Redis4j, cache put error for key: {} with value: {} in cache: {}", key, value, cache.getName(), exception);
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        logger.error("Redis4j, cache evict error for key: {} in cache: {}", key, cache.getName(), exception);
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        logger.error("Redis4j, cache clear error in cache: {}", cache.getName(), exception);
    }
}
