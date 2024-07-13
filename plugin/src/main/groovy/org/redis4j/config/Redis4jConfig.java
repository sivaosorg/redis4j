package org.redis4j.config;

import org.redis4j.service.Redis4jConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableCaching
@EnableRedisRepositories
public class Redis4jConfig {
    protected final Redis4jConfigService redis4jConfigService;

    @Autowired
    public Redis4jConfig(Redis4jConfigService redis4jConfigService) {
        this.redis4jConfigService = redis4jConfigService;
    }
}
