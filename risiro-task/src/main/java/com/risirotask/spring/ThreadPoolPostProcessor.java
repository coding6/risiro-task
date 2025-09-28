package com.risirotask.spring;

import com.risirotask.core.executor.DynamicThreadPoolExecutor;
import com.risirotask.core.manager.ThreadPoolManager;
import com.risirotask.core.metrics.reporter.MetricsReporter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @author coding6
 * @create 2025/9/20
 * @description
 */
public class ThreadPoolPostProcessor implements BeanPostProcessor {

    private final ThreadPoolManager manager;

    private final MetricsReporter reporter;

    public ThreadPoolPostProcessor(ThreadPoolManager manager, MetricsReporter reporter) {
        this.manager = manager;
        this.reporter = reporter;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DynamicThreadPoolExecutor executor) {
            executor.setMetricsCollector(reporter);
            // 确保线程池被注册
            manager.register(executor);
            executor.scheduleMetricsCollection();
        }
        return bean;
    }
}
