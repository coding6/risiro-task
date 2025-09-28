package com.risirotask.core.metrics.config;

import com.risirotask.config.ThreadPoolConfig;

import java.util.function.Consumer;

/**
 * @author coding6
 * @create 2025/9/27
 * @description
 */
public class DefaultConfigCenter implements ConfigCenter {
    @Override
    public void watch(String poolName, Consumer<ThreadPoolConfig> callBack) {

    }

    @Override
    public void getConfig() {

    }
}
