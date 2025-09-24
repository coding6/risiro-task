package com.risirotask.core.config;

import com.risirotask.core.manager.ThreadPoolManager;
import com.risirotask.core.metrics.reporter.factory.MetricsReporterFactory;
import com.risirotask.spring.ThreadPoolPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
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
    public MetricsReporterFactory monitorReporterFactory(
                                        ThreadPoolMonitorProperties monitorProperties,
                                        ApplicationContext applicationContext) {
        return new MetricsReporterFactory(monitorProperties, applicationContext);
    }

    @Bean
    public ThreadPoolManager threadPoolManager(ThreadPoolProperties properties)  {
        return new ThreadPoolManager(properties);
    }

    @Bean
    public ThreadPoolPostProcessor threadPoolPostProcessor() {
        return new ThreadPoolPostProcessor();
    }
}
