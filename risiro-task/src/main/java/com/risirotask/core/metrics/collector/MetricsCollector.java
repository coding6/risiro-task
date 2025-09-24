package com.risirotask.core.metrics.collector;

import com.risirotask.core.executor.DynamicThreadPoolExecutor;
import com.risirotask.core.metrics.enu.ThreadPoolMetrics;
import com.risirotask.core.metrics.reporter.MetricsReporter;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author coding6
 * @create 2025/9/20
 * @description
 */
public class MetricsCollector {

    private final MetricsReporter reporter;

    private final String poolName;

    public MetricsCollector(MetricsReporter reporter, String poolName) {
        this.reporter = reporter;
        this.poolName = poolName;
        this.reporter.init(poolName);
    }

    public void collect(DynamicThreadPoolExecutor executor) {
        Map<ThreadPoolMetrics, Number> metrics = new EnumMap<>(ThreadPoolMetrics.class);
        // 核心指标
        metrics.put(ThreadPoolMetrics.CORE_POOL_SIZE, executor.getCorePoolSize());
        metrics.put(ThreadPoolMetrics.MAX_POOL_SIZE, executor.getMaximumPoolSize());
        metrics.put(ThreadPoolMetrics.ACTIVE_THREADS, executor.getActiveCount());
        metrics.put(ThreadPoolMetrics.QUEUE_SIZE, executor.getQueue().size());
        metrics.put(ThreadPoolMetrics.QUEUE_CAPACITY, executor.getWorkQueue().getCapacity());

        reporter.report(poolName, metrics);
    }
}
