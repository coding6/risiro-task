package com.risirotask.core.manager;

import com.risirotask.config.ThreadPoolProperties;
import com.risirotask.core.executor.DynamicThreadPoolExecutor;
import com.risirotask.core.metrics.config.ConfigCenter;
import org.springframework.beans.factory.DisposableBean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author coding6
 * @create 2025/9/20
 * @description
 */
public class ThreadPoolManager implements DisposableBean {

    private final ThreadPoolProperties threadPoolProperties;

    public static final Map<String, DynamicThreadPoolExecutor> THREAD_POOL_REGISTRY = new ConcurrentHashMap<>();

    private final ConfigCenter configCenter;

    public ThreadPoolManager(ThreadPoolProperties threadPoolProperties, ConfigCenter configCenter) {
        this.threadPoolProperties = threadPoolProperties;
        this.configCenter = configCenter;
    }

    public void register(DynamicThreadPoolExecutor threadPoolExecutor) {
        THREAD_POOL_REGISTRY.put(threadPoolExecutor.getPoolName(), threadPoolExecutor);
        configCenter.watch(threadPoolExecutor.getPoolName(), threadPoolExecutor::updateConfig);
    }

    @Override
    public void destroy() throws Exception{
        showdown();
    }

    private void showdown() {
        if (threadPoolProperties.isWaitForTasksToCompleteOnShutdown()) {
            THREAD_POOL_REGISTRY.values().forEach(DynamicThreadPoolExecutor::shutdown);
        } else {
            THREAD_POOL_REGISTRY.values().forEach(DynamicThreadPoolExecutor::shutdownNow);
        }
        awaitTerminationIfNecessary();
    }

    private void awaitTerminationIfNecessary() {
        if (threadPoolProperties.getAwaitTerminationSeconds() > 0) {
            THREAD_POOL_REGISTRY.values().forEach(executor -> {
                try {
                    executor.awaitTermination(threadPoolProperties.getAwaitTerminationSeconds(), TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
    }
}
