package com.risirotask.config;

import com.risirotask.core.manager.ThreadPoolManager;
import com.risirotask.core.metrics.config.ConfigCenter;
import com.risirotask.core.metrics.reporter.MetricsReporter;
import com.risirotask.spring.ThreadPoolPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author coding6
 * @create 2025/9/20
 * @description
 */
@Configuration
@ConditionalOnProperty(prefix = "risiro-task.thread-pool", name = "enabled", havingValue = "true")
@EnableConfigurationProperties({ThreadPoolProperties.class, ThreadPoolMonitorProperties.class})
public class DynamicThreadPoolAutoConfiguration {

    @Bean
    public ThreadPoolManager threadPoolManager(ThreadPoolProperties properties, ConfigCenter configCenter)  {
        return new ThreadPoolManager(properties, configCenter);
    }

    @Bean
    public ThreadPoolPostProcessor threadPoolPostProcessor(ThreadPoolManager manager, MetricsReporter reporter) {
        return new ThreadPoolPostProcessor(manager, reporter);
    }
}
