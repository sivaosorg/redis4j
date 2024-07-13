package org.redis4j.config.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.Duration;

@SuppressWarnings({""})
@Component
@ConfigurationProperties(prefix = "spring.redis4j")
public class Redis4jProperties implements Serializable {
    public Redis4jProperties() {
        super();
    }

    private boolean enabled = false;
    private boolean debugging = false;
    private boolean testOnBorrow = true; // test_on_borrow
    private boolean testOnReturn = true; // test_on_return
    private boolean testWhileIdle = true; // test_while_idle
    private boolean blockWhenExhausted = true; // block_when_exhausted
    private boolean sharedNativeConnection = true; // shared_native_connection
    private int numTestsPerEvictionRun = 3; // num_tests_per_eviction_run
    private Duration minEvictIdleDuration; // min_evict_idle_duration
    private Duration durationBetweenEvictionRuns; // duration_between_eviction_runs
    private Duration executionCommandTimeout; // execution_command_timeout

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

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public boolean isBlockWhenExhausted() {
        return blockWhenExhausted;
    }

    public void setBlockWhenExhausted(boolean blockWhenExhausted) {
        this.blockWhenExhausted = blockWhenExhausted;
    }

    public int getNumTestsPerEvictionRun() {
        return numTestsPerEvictionRun;
    }

    public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
        this.numTestsPerEvictionRun = numTestsPerEvictionRun;
    }

    public Duration getMinEvictIdleDuration() {
        return minEvictIdleDuration;
    }

    public void setMinEvictIdleDuration(Duration minEvictIdleDuration) {
        this.minEvictIdleDuration = minEvictIdleDuration;
    }

    public Duration getDurationBetweenEvictionRuns() {
        return durationBetweenEvictionRuns;
    }

    public void setDurationBetweenEvictionRuns(Duration durationBetweenEvictionRuns) {
        this.durationBetweenEvictionRuns = durationBetweenEvictionRuns;
    }

    public Duration getExecutionCommandTimeout() {
        return executionCommandTimeout;
    }

    public void setExecutionCommandTimeout(Duration executionCommandTimeout) {
        this.executionCommandTimeout = executionCommandTimeout;
    }

    public boolean isSharedNativeConnection() {
        return sharedNativeConnection;
    }

    public void setSharedNativeConnection(boolean sharedNativeConnection) {
        this.sharedNativeConnection = sharedNativeConnection;
    }

    @Override
    public String toString() {
        return String.format("Redis4j { enabled: %s, debugging: %s, test_on_borrow: %s, test_on_return: %s, test_while_idle: %s, block_when_exhausted: %s, num_tests_per_eviction_run: %d, min_evict_idle_duration: %s, duration_between_eviction_runs: %s, execution_command_timeout: %s, shared_native_connection: %s }",
                enabled, debugging, testOnBorrow, testOnReturn, testWhileIdle, blockWhenExhausted, numTestsPerEvictionRun, minEvictIdleDuration, durationBetweenEvictionRuns, executionCommandTimeout, sharedNativeConnection);
    }
}
