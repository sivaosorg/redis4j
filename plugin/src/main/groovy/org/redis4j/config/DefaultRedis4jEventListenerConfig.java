package org.redis4j.config;

import io.lettuce.core.event.connection.ConnectionActivatedEvent;
import io.lettuce.core.event.connection.ConnectionDeactivatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;

//@Component
public class DefaultRedis4jEventListenerConfig {
    protected static final Logger logger = LoggerFactory.getLogger(DefaultRedis4jEventListenerConfig.class);

    @EventListener
    public void onConnectionActivated(ConnectionActivatedEvent event) {
        logger.info("Connection activated event: {}", event.toString());
        // drafting
    }

    @EventListener
    public void onConnectionDeactivated(ConnectionDeactivatedEvent event) {
        logger.error("Connection deactivated event: {}", event.toString());
        // drafting
    }
}
