package org.redis4j.service.impl;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.redis4j.config.props.Redis4jProperties;
import org.redis4j.service.Redis4jConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;
import org.unify4j.common.Object4j;
import org.unify4j.common.String4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

import javax.annotation.PostConstruct;
import java.time.Duration;

@SuppressWarnings({"FieldCanBeLocal", "DuplicatedCode"})
@Service
public class Redis4jConfigServiceImpl implements Redis4jConfigService {
    protected static final Logger logger = LoggerFactory.getLogger(Redis4jConfigServiceImpl.class);

    protected final Redis4jProperties properties;
    protected final RedisProperties redisProperties;

    @Autowired
    public Redis4jConfigServiceImpl(Redis4jProperties properties,
                                    RedisProperties redisProperties) {
        this.properties = properties;
        this.redisProperties = redisProperties;
    }

    @PostConstruct
    public void initiate() {
        if (this.isDebugging()) {
            logger.info(properties.toString());
        }
    }

    /**
     * @return true if the Redis4J enabled, false otherwise
     */
    @Override
    public boolean isEnabled() {
        return properties.isEnabled();
    }

    /**
     * @return true if the Redis4J configured debugging, false otherwise
     */
    @Override
    public boolean isDebugging() {
        return properties.isDebugging();
    }

    /**
     * Creates and configures a JedisPoolConfig object based on application.yml properties.
     * Retrieves Redis connection and pooling settings from RedisProperties and Redis4jProperties.
     *
     * @return Configured JedisPoolConfig instance for Redis connection pooling, class {@link JedisPoolConfig}
     */
    @Override
    public JedisPoolConfig createDefaultPoolConfig() {
        return this.createPoolConfig(properties, redisProperties);
    }

    /**
     * Creates and configures a JedisPoolConfig instance based on provided Redis4jProperties and RedisProperties.
     *
     * @param properties      The Redis4jProperties containing Redis4J-specific configuration properties, class {@link Redis4jProperties}
     * @param redisProperties The RedisProperties containing general Redis connection and pooling properties, class {@link RedisProperties}
     * @return Initialized JedisPoolConfig instance configured with the specified properties, class {@link JedisPoolConfig}
     */
    @Override
    public JedisPoolConfig createPoolConfig(Redis4jProperties properties, RedisProperties redisProperties) {
        final JedisPoolConfig config = new JedisPoolConfig();
        RedisProperties.Pool pool = redisProperties.getJedis().getPool();
        config.setTestOnBorrow(properties.isTestOnBorrow());
        config.setTestOnReturn(properties.isTestOnReturn());
        config.setTestWhileIdle(properties.isTestWhileIdle());
        config.setBlockWhenExhausted(properties.isBlockWhenExhausted());
        config.setNumTestsPerEvictionRun(properties.getNumTestsPerEvictionRun());
        config.setTimeBetweenEvictionRuns(properties.getDurationBetweenEvictionRuns());
        if (Object4j.allNotNull(pool)) {
            config.setMaxTotal(pool.getMaxActive());
            config.setMaxIdle(pool.getMaxIdle());
            config.setMinIdle(pool.getMinIdle());
            config.setMaxWait(Duration.ofMillis(pool.getMaxWait().toMillis()));
        }
        return config;
    }

    /**
     * Creates a JedisPool instance based on the provided JedisPoolConfig and Redis connection properties.
     * Uses timeout duration from RedisProperties for pool creation.
     *
     * @param pool The configured JedisPoolConfig instance.
     * @return Initialized JedisPool object for managing Redis connections, class {@link JedisPool}
     */
    @Override
    public JedisPool createPool(JedisPoolConfig pool) {
        Duration duration = redisProperties.getTimeout();
        long timeoutMillis = duration != null ? duration.toMillis() : 20000; // Default to 20 seconds if duration is null
        return new JedisPool(pool,
                redisProperties.getHost(),
                redisProperties.getPort(),
                (int) timeoutMillis,
                redisProperties.getPassword());
    }

    /**
     * Retrieves a Jedis client from the provided JedisPool.
     *
     * @param pool The JedisPool instance from which to retrieve a Jedis client, class {@link JedisPool}
     * @return A Jedis client obtained from the pool, or null if the pool is null, class {@link Jedis}
     */
    @Override
    public Jedis createClient(JedisPool pool) {
        try {
            return pool == null ? null : pool.getResource();
        } catch (JedisException e) {
            logger.error("\uD83D\uDD34 Redis4j, creating Jedis client got an exception: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Constructs a RedisStandaloneConfiguration instance based on the Redis connection properties.
     * Configures host name, port, and optional password for standalone Redis server connection.
     *
     * @return RedisStandaloneConfiguration object with configured connection details, class {@link RedisStandaloneConfiguration}
     */
    @Override
    public RedisStandaloneConfiguration getDefaultStandaloneConfig() {
        return this.createStandaloneConfig(redisProperties);
    }

    /**
     * Constructs a RedisStandaloneConfiguration instance based on the Redis connection properties.
     * Configures host name, port, and optional password for standalone Redis server connection.
     *
     * @param properties the Redis properties configuration, class {@link RedisProperties}
     * @return RedisStandaloneConfiguration object with configured connection details, class {@link RedisStandaloneConfiguration}
     */
    @Override
    public RedisStandaloneConfiguration createStandaloneConfig(RedisProperties properties) {
        if (properties == null) {
            return null;
        }
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(properties.getHost());
        config.setPort(properties.getPort());
        config.setPassword(RedisPassword.of(properties.getPassword()));
        return config;
    }

    /**
     * Creates a LettucePoolingClientConfiguration instance for configuring Lettuce Redis client pooling.
     * Uses the provided JedisPoolConfig to set up pooling configuration and specifies a command timeout duration.
     *
     * @param pool The configured JedisPoolConfig instance for Lettuce client pooling, class {@link JedisPoolConfig}
     * @return Initialized LettucePoolingClientConfiguration object with specified pooling and timeout settings, class {@link LettucePoolingClientConfiguration}
     */
    @Override
    public LettucePoolingClientConfiguration createLettucePoolingClientConfig(JedisPoolConfig pool) {
        Duration commandTimeout = properties.getExecutionCommandTimeout() != null ? properties.getExecutionCommandTimeout() : Duration.ofSeconds(100);
        return LettucePoolingClientConfiguration.builder()
                .poolConfig(pool)
                .commandTimeout(commandTimeout)
                .build();
    }

    /**
     * Creates a LettuceConnectionFactory instance for establishing a connection to a Redis server.
     * Uses the provided RedisStandaloneConfiguration and LettucePoolingClientConfiguration to configure the connection factory.
     * Sets the option to share native connections across multiple clients.
     *
     * @param standaloneConfig    The RedisStandaloneConfiguration containing Redis server connection details, class {@link RedisStandaloneConfiguration}
     * @param poolingClientConfig The LettucePoolingClientConfiguration specifying client pooling and timeout settings, class {@link LettucePoolingClientConfiguration}
     * @return Initialized LettuceConnectionFactory object configured with the provided Redis and pooling configurations, class {@link LettuceConnectionFactory}
     */
    @Override
    public LettuceConnectionFactory createLettuceConnectionFactory(RedisStandaloneConfiguration standaloneConfig, LettucePoolingClientConfiguration poolingClientConfig) {
        LettuceConnectionFactory factory = new LettuceConnectionFactory(standaloneConfig, poolingClientConfig);
        factory.setShareNativeConnection(properties.isSharedNativeConnection()); // Enable sharing of native connections across multiple clients
        return factory;
    }

    /**
     * Creates a Jackson2JsonRedisSerializer instance for serializing and deserializing objects to/from JSON in Redis.
     * Configures the Jackson ObjectMapper with specific settings for Redis serialization.
     *
     * @return Initialized Jackson2JsonRedisSerializer configured with custom ObjectMapper settings, class {@link Jackson2JsonRedisSerializer}
     */
    @Override
    public Jackson2JsonRedisSerializer<Object> createJsonRedisSerializer() {
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        JsonFactory factory = new JsonFactory();
        factory.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);

        ObjectMapper mapper = new ObjectMapper(factory);
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES); // Disable deserialization feature for unknown properties

        serializer.setObjectMapper(mapper);
        return serializer;
    }

    /**
     * Creates a RedisTemplate instance for interacting with Redis using Spring Data Redis.
     * Configures connection factory, serializers for keys and values, and enables transaction support.
     *
     * @param factory           The LettuceConnectionFactory used to create Redis connections, class {@link LettuceConnectionFactory}
     * @param jacksonSerializer The Jackson2JsonRedisSerializer used to serialize/deserialize values to/from JSON, class {@link Jackson2JsonRedisSerializer}
     * @param serializer        The RedisSerializer used for serializing Redis keys, class {@link RedisSerializer}
     * @return Initialized RedisTemplate configured with specified connection factory and serializers, class {@link RedisTemplate}
     */
    @Override
    public RedisTemplate<String, Object> createRedisDispatch(LettuceConnectionFactory factory, Jackson2JsonRedisSerializer<Object> jacksonSerializer, RedisSerializer<String> serializer) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(serializer); // Serializer for Redis keys
        template.setValueSerializer(jacksonSerializer); // Serializer for Redis values
        template.setHashKeySerializer(serializer); // Serializer for Redis hash keys
        template.setHashValueSerializer(jacksonSerializer); // Serializer for Redis hash values
        template.setEnableTransactionSupport(true); // Enable transaction support for RedisTemplate
        template.afterPropertiesSet(); // Perform any necessary initialization after setting properties
        return template;
    }

    /**
     * Creates a StringRedisTemplate instance for interacting with Redis using Spring Data Redis.
     * Configures connection factory, serializers for keys and values, and enables transaction support.
     *
     * @param factory           The LettuceConnectionFactory used to create Redis connections, class {@link LettuceConnectionFactory}
     * @param jacksonSerializer The Jackson2JsonRedisSerializer used to serialize/deserialize values to/from JSON, class {@link Jackson2JsonRedisSerializer}
     * @param serializer        The RedisSerializer used for serializing Redis keys, class {@link RedisSerializer}
     * @return Initialized StringRedisTemplate configured with specified connection factory and serializers, class {@link StringRedisTemplate}
     */
    @Override
    public StringRedisTemplate createStringRedisDispatch(LettuceConnectionFactory factory, Jackson2JsonRedisSerializer<Object> jacksonSerializer, RedisSerializer<String> serializer) {
        StringRedisTemplate template = new StringRedisTemplate(factory);
        template.setKeySerializer(serializer); // Serializer for Redis keys
        template.setValueSerializer(jacksonSerializer); // Serializer for Redis values
        template.setHashKeySerializer(serializer); // Serializer for Redis hash keys
        template.setHashValueSerializer(jacksonSerializer); // Serializer for Redis hash values
        template.setEnableTransactionSupport(true); // Enable transaction support for StringRedisTemplate
        template.afterPropertiesSet(); // Perform any necessary initialization after setting properties
        return template;
    }

    /**
     * Creates a CacheManager instance for managing caches backed by Redis using Spring Data Redis.
     * Configures the cache configuration and Redis cache writer.
     *
     * @param factory The LettuceConnectionFactory used to create Redis connections, class {@link LettuceConnectionFactory}
     * @return Initialized CacheManager configured with the specified connection factory, class {@link CacheManager}
     */
    @Override
    public CacheManager createCacheManager(LettuceConnectionFactory factory) {
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig();
        return RedisCacheManager.builder(
                        RedisCacheWriter.nonLockingRedisCacheWriter(factory))
                .cacheDefaults(cacheConfig)
                .build();
    }

    /**
     * Checks if a Redis connection factory is connected.
     * Returns true if the factory is not null and a connection can be established without errors.
     * Uses pipelined connection status for checking.
     *
     * @param factory The RedisConnectionFactory to check, class {@link RedisConnectionFactory}
     * @return true if the factory is connected and can perform pipelined operations; false otherwise.
     */
    @Override
    public boolean isConnected(RedisConnectionFactory factory) {
        if (factory == null) {
            return false;
        }
        try {
            String status = factory.getConnection().ping();
            if (this.isDebugging()) {
                logger.info("Verifying Redis Server ping: {}", status);
            }
            return String4j.isNotEmpty(status);
        } catch (Exception e) {
            logger.error("Checking Redis connection got an exception: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Checks if a Redis connection template is connected.
     * Returns true if the template is not null and a connection can be established without errors.
     * Uses pipelined connection status for checking.
     *
     * @param template The RedisConnectionFactory to check, class {@link RedisConnectionFactory}
     * @return true if the template is connected and can perform pipelined operations; false otherwise.
     */
    @Override
    public boolean isConnected(RedisTemplate<String, Object> template) {
        if (template == null || template.getConnectionFactory() == null) {
            return false;
        }
        return this.isConnected(template.getConnectionFactory());
    }
}
