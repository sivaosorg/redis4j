package org.redis4j.config.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@SuppressWarnings({""})
@Component
@ConfigurationProperties(prefix = "spring.redis4j")
public class Redis4jProperties implements Serializable {
    public Redis4jProperties() {
        super();
    }

    private boolean enabled = false;
    private boolean debugging = false;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isDebugging() {
        return debugging;
    }

    public void setDebugging(boolean debugging) {
        this.debugging = debugging;
    }

    @Override
    public String toString() {
        return String.format("Redis4j { enabled: %s, debugging: %s }", enabled, debugging);
    }
}
