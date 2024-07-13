package org.redis4j.common;

import org.redis4j.config.Redis4jBeanConfig;
import org.redis4j.config.Redis4jStatusConfig;
import org.redis4j.service.Redis4jConfigService;
import org.redis4j.service.impl.Redis4jConfigServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.unify4j.common.Object4j;

public class Redis4j {
    protected static final Logger logger = LoggerFactory.getLogger(Redis4j.class);
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
        return e.isRedisDispatchAvailable();
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
}
