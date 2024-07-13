package org.redis4j.config;

import org.redis4j.service.Redis4jConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@EnableCaching
@EnableRedisRepositories
public class Redis4jConfig {
    protected final Redis4jConfigService redis4jConfigService;

    @Autowired
    public Redis4jConfig(Redis4jConfigService redis4jConfigService) {
        this.redis4jConfigService = redis4jConfigService;
    }

    @Bean
    @ConditionalOnMissingBean(name = "defaultPoolConfig")
    public JedisPoolConfig defaultPoolConfig() {
        return redis4jConfigService.createDefaultPoolConfig();
    }

    @Bean
    @ConditionalOnMissingBean(name = "jedisPool")
    public JedisPool jedisPool() {
        return redis4jConfigService.createPool(this.defaultPoolConfig());
    }

    @Bean
    public Jedis jedisClient() {
        return redis4jConfigService.createClient(this.jedisPool());
    }

    @Bean
    public RedisStandaloneConfiguration defaultStandaloneConfig() {
        return redis4jConfigService.getDefaultStandaloneConfig();
    }

    @Bean
    public LettucePoolingClientConfiguration lettucePoolingClientConfig() {
        return redis4jConfigService.createLettucePoolingClientConfig(this.defaultPoolConfig());
    }

    @Bean
    public LettuceConnectionFactory factory() {
        return redis4jConfigService.createLettuceConnectionFactory(this.defaultStandaloneConfig(), this.lettucePoolingClientConfig());
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        return redis4jConfigService.createRedisDispatch(this.factory(), redis4jConfigService.createJsonRedisSerializer(), new StringRedisSerializer());
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate() {
        return redis4jConfigService.createStringRedisDispatch(this.factory(), redis4jConfigService.createJsonRedisSerializer(), new StringRedisSerializer());
    }

    @Bean
    public CacheManager cacheManager() {
        return redis4jConfigService.createCacheManager(this.factory());
    }
}
