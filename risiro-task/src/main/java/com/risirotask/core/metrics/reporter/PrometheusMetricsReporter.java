package com.risirotask.core.metrics.reporter;

import com.risirotask.core.metrics.enu.ThreadPoolMetrics;

import java.util.Map;

/**
 * @author coding6
 * @create 2025/9/20
 * @description
 */
public class PrometheusMetricsReporter implements MetricsReporter {

    @Override
    public void init(String poolName) {

    }

    @Override
    public void report(String poolName, Map<ThreadPoolMetrics, Number> metrics) {

    }
}
