package com.risirotask.core.metrics.reporter;

import com.risirotask.core.metrics.enu.ThreadPoolMetrics;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author coding6
 * @create 2025/9/20
 * @description
 */
@Slf4j
public class PrometheusMetricsReporter implements MetricsReporter {

    private final Map<String, Map<ThreadPoolMetrics, Gauge>> gaugeCache = new ConcurrentHashMap<>();

    private final MeterRegistry registry;

    public PrometheusMetricsReporter(MeterRegistry registry) {
        this.registry =  registry;
    }

    @Override
    public void report(String poolName, Map<ThreadPoolMetrics, Number> metrics) {
        log.info("ThreadPool {} metrics: {}", poolName, metrics);
        // 获取或创建该线程池的指标缓存
        Map<ThreadPoolMetrics, Gauge> poolGauges = gaugeCache.computeIfAbsent(
                poolName, k -> new ConcurrentHashMap<>()
        );
        metrics.forEach((metric, currentValue) ->
            poolGauges.computeIfAbsent(metric, type ->
                    Gauge.builder(metric.getCode(), () -> {
                            // 动态获取最新值
                            Number latestValue = metrics.get(type);
                            return latestValue != null ? latestValue.doubleValue() : Double.NaN;
                        })
                        .tags(Tags.of("pool_name", poolName))
                        .description(metric.getName())
                        .register(registry)
            )
        );
    }
}
