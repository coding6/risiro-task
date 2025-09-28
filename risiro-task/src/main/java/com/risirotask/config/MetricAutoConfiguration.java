package com.risirotask.config;

import com.risirotask.core.metrics.reporter.DefaultMetricsReporter;
import com.risirotask.core.metrics.reporter.MetricsReporter;
import com.risirotask.core.metrics.reporter.PrometheusMetricsReporter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.hotspot.DefaultExports;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author coding6
 * @create 2025/9/27
 * @description
 */
@Configuration
public class MetricAutoConfiguration {

    @PostConstruct
    public void init() {
        // 注册JVM指标，这些指标将通过Spring Boot Actuator的/actuator/prometheus端点暴露
        DefaultExports.initialize();
        System.out.println("Prometheus metrics available at: http://localhost:8081/actuator/prometheus");
    }

    @Bean
    @ConditionalOnProperty(prefix = "risiro-task.monitor", name = "type", havingValue = "prometheus")
    @ConditionalOnClass(name = "io.prometheus.client.CollectorRegistry")
    public MetricsReporter prometheusMetricsReporter(MeterRegistry meterRegistry) {
        return new PrometheusMetricsReporter(meterRegistry);
    }

    //兜底实现，什么都不接入直接返回默认的监控reporter
    @Bean
    @ConditionalOnMissingBean(MetricsReporter.class)
    public MetricsReporter defaultMetricsReporter() {
        return new DefaultMetricsReporter();
    }
}
