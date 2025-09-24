package org.example;

import com.risirotask.core.config.ThreadPoolConfig;
import com.risirotask.core.executor.DynamicThreadPoolExecutor;
import com.risirotask.core.manager.ThreadPoolManager;
import com.risirotask.core.metrics.reporter.MetricsReporter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author coding6
 * @create 2025/9/20
 * @description
 */
@Configuration
public class ThreadPoolConfiguration {

    @Bean
    public DynamicThreadPoolExecutor orderThreadPool() {
        ThreadPoolConfig config = new ThreadPoolConfig("order-thread-pool",
                4,
                16,
                60,
                TimeUnit.SECONDS,
                1000,
                new ThreadPoolExecutor.AbortPolicy());

        return new DynamicThreadPoolExecutor(config);
    }
}
