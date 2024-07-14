package org.redis4j.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.unify4j.model.enums.IconType;

@Component
@ConditionalOnProperty(
        value = "spring.redis4j.enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class Redis4jStartupConfig implements ApplicationRunner {
    protected final static Logger logger = LoggerFactory.getLogger(Redis4jStartupConfig.class);

    protected final Redis4jStatusConfig config;

    @Autowired
    public Redis4jStartupConfig(Redis4jStatusConfig config) {
        this.config = config;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (config.isConnected()) {
            logger.info("{} Redis4j startup, connected Redis Server successfully", IconType.SUCCESS.getCode());
        } else {
            logger.error("{} Redis4j startup, unable to connect to Redis Server", IconType.ERROR.getCode());
            return;
        }
        if (config.isDispatchAvailable()) {
            logger.info("{} Redis4j startup, RedisTemplate<String, Object> created successfully", IconType.SUCCESS.getCode());
        } else {
            logger.error("{} Redis4j startup, unable to create RedisTemplate<String, Object>", IconType.ERROR.getCode());
        }
        if (config.isStringDispatchAvailable()) {
            logger.info("{} Redis4j startup, StringRedisTemplate created successfully", IconType.SUCCESS.getCode());
        } else {
            logger.error("{} Redis4j startup, unable to create StringRedisTemplate", IconType.ERROR.getCode());
        }
    }
}
