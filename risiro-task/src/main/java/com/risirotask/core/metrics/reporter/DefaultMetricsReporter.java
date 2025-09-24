package com.risirotask.core.metrics.reporter;

import com.risirotask.core.metrics.enu.ThreadPoolMetrics;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author coding6
 * @create 2025/9/20
 * @description 默认指标上报器，做一些日志操作
 */
@Slf4j
public class DefaultMetricsReporter implements MetricsReporter {
    @Override
    public void init(String poolName) {}

    @Override
    public void report(String poolName, Map<ThreadPoolMetrics, Number> metrics) {
        String metricsStr = metrics.entrySet().stream()
                .map(e -> e.getKey().getCode() + "=" + e.getValue())
                .collect(Collectors.joining(", "));
        log.info("ThreadPool {} metrics: {}", poolName, metrics);
    }
}
