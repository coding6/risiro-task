package com.risirotask.core.manager;

import com.risirotask.core.config.ThreadPoolConfig;
import com.risirotask.core.config.ThreadPoolProperties;
import com.risirotask.core.executor.DynamicThreadPoolExecutor;
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

    public ThreadPoolManager(ThreadPoolProperties threadPoolProperties) {
        this.threadPoolProperties = threadPoolProperties;
    }

    public DynamicThreadPoolExecutor getThreadPool(String poolName) {
        return THREAD_POOL_REGISTRY.get(poolName);
    }

    public void register(DynamicThreadPoolExecutor threadPoolExecutor) {
        THREAD_POOL_REGISTRY.put(threadPoolExecutor.getPoolName(), threadPoolExecutor);
    }

    public boolean updateConfig(String poolName, ThreadPoolConfig newConfig) {
        DynamicThreadPoolExecutor executor = THREAD_POOL_REGISTRY.get(poolName);
        if (executor != null) {
            executor.updateConfig(newConfig);
            return true;
        }
        return false;
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
