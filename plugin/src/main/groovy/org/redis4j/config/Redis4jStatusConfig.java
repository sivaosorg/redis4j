package org.redis4j.config;

import org.redis4j.service.Redis4jConfigService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@ConditionalOnProperty(
        value = "spring.redis4j.enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class Redis4jStatusConfig {
    protected final Redis4jConfigService service;
    protected final RedisConnectionFactory factory;
    protected final StringRedisTemplate stringRedisTemplate;
    protected final RedisTemplate<String, Object> redisTemplate;

    public Redis4jStatusConfig(Redis4jConfigService service,
                               RedisConnectionFactory factory,
                               StringRedisTemplate stringRedisTemplate,
                               RedisTemplate<String, Object> redisTemplate) {
        this.service = service;
        this.factory = factory;
        this.stringRedisTemplate = stringRedisTemplate;
        this.redisTemplate = redisTemplate;
    }

    /**
     * Checks if a Redis connection factory is connected.
     * Returns true if the factory is not null and a connection can be established without errors.
     * Uses pipelined connection status for checking.
     *
     * @return true if the factory is connected and can perform pipelined operations; false otherwise.
     */
    public boolean isConnected() {
        return this.service.isConnected(this.factory);
    }

    /**
     * Checks if the Redis configuration service can be executed.
     * Returns true if the Redis connection factory is connected and the service is enabled.
     *
     * @return true if the service is executed; false otherwise.
     */
    public boolean canExecuted() {
        return this.isConnected() && service.isEnabled();
    }

    /**
     * Checks if the Redis dispatch template is available.
     *
     * @return true if the Redis dispatch template is initialized; false otherwise.
     */
    public boolean isRedisDispatchAvailable() {
        return this.redisTemplate != null;
    }

    /**
     * Checks if the String dispatch template is available.
     *
     * @return true if the String dispatch template is initialized; false otherwise.
     */
    public boolean isStringDispatchAvailable() {
        return this.redisTemplate != null;
    }

    /**
     * Provides the Redis dispatch template.
     *
     * @return the RedisTemplate<String, Object> instance.
     */
    public RedisTemplate<String, Object> dispatch() {
        return this.redisTemplate;
    }

    /**
     * Provides the String dispatch template.
     *
     * @return the StringRedisTemplate instance.
     */
    public StringRedisTemplate stringDispatch() {
        return this.stringRedisTemplate;
    }
}
