package com.risirotask.core.metrics.config;

import com.risirotask.config.ThreadPoolConfig;

import java.util.function.Consumer;

/**
 * @author coding6
 * @create 2025/9/24
 * @description
 */
public interface ConfigCenter{

    void watch(String poolName, Consumer<ThreadPoolConfig> callBack);

    void getConfig();
}
