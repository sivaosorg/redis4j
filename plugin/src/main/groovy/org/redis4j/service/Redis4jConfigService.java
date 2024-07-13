package org.redis4j.service;

import org.redis4j.config.props.Redis4jProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public interface Redis4jConfigService {

    boolean isEnabled();

    boolean isDebugging();

    /**
     * Creates and configures a JedisPoolConfig object based on application.yml properties.
     * Retrieves Redis connection and pooling settings from RedisProperties and Redis4jProperties.
     *
     * @return Configured JedisPoolConfig instance for Redis connection pooling, class {@link JedisPoolConfig}
     */
    JedisPoolConfig createDefaultPoolConfig();

    /**
     * Creates and configures a JedisPoolConfig instance based on provided Redis4jProperties and RedisProperties.
     *
     * @param properties      The Redis4jProperties containing Redis4J-specific configuration properties, class {@link Redis4jProperties}
     * @param redisProperties The RedisProperties containing general Redis connection and pooling properties, class {@link RedisProperties}
     * @return Initialized JedisPoolConfig instance configured with the specified properties, class {@link JedisPoolConfig}
     */
    JedisPoolConfig createPoolConfig(Redis4jProperties properties, RedisProperties redisProperties);

    /**
     * Creates a JedisPool instance based on the provided JedisPoolConfig and Redis connection properties.
     * Uses timeout duration from RedisProperties for pool creation.
     *
     * @param pool The configured JedisPoolConfig instance.
     * @return Initialized JedisPool object for managing Redis connections, class {@link JedisPool}
     */
    JedisPool createPool(JedisPoolConfig pool);

    /**
     * Retrieves a Jedis client from the provided JedisPool.
     *
     * @param pool The JedisPool instance from which to retrieve a Jedis client, class {@link JedisPool}
     * @return A Jedis client obtained from the pool, or null if the pool is null, class {@link Jedis}
     */
    Jedis createClient(JedisPool pool);

    /**
     * Constructs a RedisStandaloneConfiguration instance based on the Redis connection properties.
     * Configures host name, port, and optional password for standalone Redis server connection.
     *
     * @return RedisStandaloneConfiguration object with configured connection details, class {@link RedisStandaloneConfiguration}
     */
    RedisStandaloneConfiguration getDefaultStandaloneConfig();

    /**
     * Constructs a RedisStandaloneConfiguration instance based on the Redis connection properties.
     * Configures host name, port, and optional password for standalone Redis server connection.
     *
     * @return RedisStandaloneConfiguration object with configured connection details, class {@link RedisStandaloneConfiguration}
     */
    RedisStandaloneConfiguration createStandaloneConfig(RedisProperties redisProperties);

    /**
     * Creates a LettucePoolingClientConfiguration instance for configuring Lettuce Redis client pooling.
     * Uses the provided JedisPoolConfig to set up pooling configuration and specifies a command timeout duration.
     *
     * @param pool The configured JedisPoolConfig instance for Lettuce client pooling, class {@link JedisPoolConfig}
     * @return Initialized LettucePoolingClientConfiguration object with specified pooling and timeout settings, class {@link LettucePoolingClientConfiguration}
     */
    LettucePoolingClientConfiguration createLettucePoolingClientConfig(JedisPoolConfig pool);

    /**
     * Creates a LettuceConnectionFactory instance for establishing a connection to a Redis server.
     * Uses the provided RedisStandaloneConfiguration and LettucePoolingClientConfiguration to configure the connection factory.
     * Sets the option to share native connections across multiple clients.
     *
     * @param standaloneConfig    The RedisStandaloneConfiguration containing Redis server connection details, class {@link RedisStandaloneConfiguration}
     * @param poolingClientConfig The LettucePoolingClientConfiguration specifying client pooling and timeout settings, class {@link LettucePoolingClientConfiguration}
     * @return Initialized LettuceConnectionFactory object configured with the provided Redis and pooling configurations, class {@link LettuceConnectionFactory}
     */
    LettuceConnectionFactory createLettuceConnectionFactory(RedisStandaloneConfiguration standaloneConfig, LettucePoolingClientConfiguration poolingClientConfig);

    /**
     * Creates a Jackson2JsonRedisSerializer instance for serializing and deserializing objects to/from JSON in Redis.
     * Configures the Jackson ObjectMapper with specific settings for Redis serialization.
     *
     * @return Initialized Jackson2JsonRedisSerializer configured with custom ObjectMapper settings, class {@link Jackson2JsonRedisSerializer}
     */
    Jackson2JsonRedisSerializer<Object> createJsonRedisSerializer();

    /**
     * Creates a RedisTemplate instance for interacting with Redis using Spring Data Redis.
     * Configures connection factory, serializers for keys and values, and enables transaction support.
     *
     * @param factory           The LettuceConnectionFactory used to create Redis connections, class {@link LettuceConnectionFactory}
     * @param jacksonSerializer The Jackson2JsonRedisSerializer used to serialize/deserialize values to/from JSON, class {@link Jackson2JsonRedisSerializer}
     * @param serializer        The RedisSerializer used for serializing Redis keys, class {@link RedisSerializer}
     * @return Initialized RedisTemplate configured with specified connection factory and serializers, class {@link RedisTemplate}
     */
    RedisTemplate<String, Object> createRedisDispatch(LettuceConnectionFactory factory, Jackson2JsonRedisSerializer<Object> jacksonSerializer, RedisSerializer<String> serializer);

    /**
     * Creates a StringRedisTemplate instance for interacting with Redis using Spring Data Redis.
     * Configures connection factory, serializers for keys and values, and enables transaction support.
     *
     * @param factory           The LettuceConnectionFactory used to create Redis connections, class {@link LettuceConnectionFactory}
     * @param jacksonSerializer The Jackson2JsonRedisSerializer used to serialize/deserialize values to/from JSON, class {@link Jackson2JsonRedisSerializer}
     * @param serializer        The RedisSerializer used for serializing Redis keys, class {@link RedisSerializer}
     * @return Initialized StringRedisTemplate configured with specified connection factory and serializers, class {@link StringRedisTemplate}
     */
    StringRedisTemplate createStringRedisDispatch(LettuceConnectionFactory factory, Jackson2JsonRedisSerializer<Object> jacksonSerializer, RedisSerializer<String> serializer);

    /**
     * Creates a CacheManager instance for managing caches backed by Redis using Spring Data Redis.
     * Configures the cache configuration and Redis cache writer.
     *
     * @param factory The LettuceConnectionFactory used to create Redis connections, class {@link LettuceConnectionFactory}
     * @return Initialized CacheManager configured with the specified connection factory, class {@link CacheManager}
     */
    CacheManager createCacheManager(LettuceConnectionFactory factory);

    /**
     * Checks if a Redis connection factory is connected.
     * Returns true if the factory is not null and a connection can be established without errors.
     * Uses pipelined connection status for checking.
     *
     * @param factory The RedisConnectionFactory to check, class {@link RedisConnectionFactory}
     * @return true if the factory is connected and can perform pipelined operations; false otherwise.
     */
    boolean isConnected(RedisConnectionFactory factory);
}
