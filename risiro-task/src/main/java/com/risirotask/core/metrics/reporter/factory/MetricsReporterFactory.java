package com.risirotask.core.metrics.reporter.factory;

import com.risirotask.core.config.ThreadPoolMonitorProperties;
import com.risirotask.core.metrics.reporter.DefaultMetricsReporter;
import com.risirotask.core.metrics.reporter.MetricsReporter;
import com.risirotask.core.metrics.reporter.PrometheusMetricsReporter;
import org.springframework.context.ApplicationContext;

/**
 * @author coding6
 * @create 2025/9/20
 * @description
 */
public class MetricsReporterFactory {
    private final ThreadPoolMonitorProperties properties;

    private final ApplicationContext  applicationContext;

    public MetricsReporterFactory(ThreadPoolMonitorProperties properties, ApplicationContext applicationContext) {
        this.properties = properties;
        this.applicationContext = applicationContext;
    }

    public MetricsReporter chooseReporter() {
        return switch (properties.getType()) {
            case LOG -> new DefaultMetricsReporter();
            case PROMETHEUS -> new PrometheusMetricsReporter();
            case CUSTOM -> createCustomReporter();
            default -> new DefaultMetricsReporter();
        };
    }

    private MetricsReporter createCustomReporter() {
        String reporterClass = properties.getCustom().getReporterClass();
        try {
            Class<?> clazz = Class.forName(reporterClass);
            if (!MetricsReporter.class.isAssignableFrom(clazz)) {
                throw new IllegalArgumentException("Custom reporter class must implement MetricsReporter interface");
            }
            return (MetricsReporter) applicationContext.getBean(clazz);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to create custom monitor reporter", e);
        }
    }
}
