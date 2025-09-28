package com.risirotask.core.metrics.reporter;

import com.risirotask.core.metrics.enu.ThreadPoolMetrics;

import java.util.Map;

/**
 * @author coding6
 * @create 2025/9/20
 * @description
 */
public interface MetricsReporter {
    void report(String poolName, Map<ThreadPoolMetrics, Number> metrics);
}
