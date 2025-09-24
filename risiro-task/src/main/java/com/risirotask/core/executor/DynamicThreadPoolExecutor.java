package com.risirotask.core.executor;

import com.risirotask.core.config.ThreadPoolConfig;
import com.risirotask.core.context.ThreadContext;
import com.risirotask.core.metrics.collector.MetricsCollector;
import com.risirotask.core.metrics.reporter.MetricsReporter;
import com.risirotask.core.queue.DynamicLinkedBlockingQueue;
import com.risirotask.core.queue.DynamicRejectedExecutionHandler;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @author coding6
 * @create 2025/9/20
 * @description
 */
@Getter
public class DynamicThreadPoolExecutor extends ThreadPoolExecutor {

    private final String poolName;

    private final DynamicLinkedBlockingQueue<Runnable> workQueue;

    private final DynamicRejectedExecutionHandler handler;

    private MetricsCollector metricsCollector;

    public DynamicThreadPoolExecutor(ThreadPoolConfig config) {
        super(config.getCorePoolSize(),
                config.getMaximumPoolSize(),
                config.getKeepAliveTime(),
                config.getUnit(),
                new DynamicLinkedBlockingQueue<>(config.getQueueCapacity()),
                config.getThreadFactory(),
                new DynamicRejectedExecutionHandler(config.getRejectedExecutionHandler()));

        this.poolName = config.getPoolName();
        this.workQueue = (DynamicLinkedBlockingQueue<Runnable>) super.getQueue();
        this.handler = (DynamicRejectedExecutionHandler) super.getRejectedExecutionHandler();
    }

    public void setMetricsCollector(MetricsReporter metricsReporter) {
        this.metricsCollector = new MetricsCollector(metricsReporter, poolName);
    }

    public void scheduleMetricsCollection() {
        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(() -> metricsCollector.collect(this),
                        1, 15, TimeUnit.SECONDS);
    }

    @Override
    public void execute(Runnable task) {
        super.execute(wrapTask(task));
    }

    private Runnable wrapTask(Runnable task) {
        Map<String, Object> capture = ThreadContext.capture();
        return () -> {
            long startTime = System.currentTimeMillis();
            try {
                //2.恢复上下文
                ThreadContext.restore(capture);
                task.run();
            } catch (Exception e) {
                throw e;
            } finally {
                //3.清理上下文
                ThreadContext.clear();
            }
        };
    }

    public void updateConfig(int corePoolSize, int maximumPoolSize, int queueCapacity) {
        // 核心线程数
        if (corePoolSize != getCorePoolSize()) {
            setCorePoolSize(corePoolSize);
        }

        // 最大线程数
        if (maximumPoolSize != getMaximumPoolSize()) {
            setMaximumPoolSize(maximumPoolSize);
        }

        // 队列容量
        if (queueCapacity != workQueue.getCapacity()) {
            workQueue.setCapacity(queueCapacity);
        }
    }

    public void updateConfig(ThreadPoolConfig newConfig) {
        // 核心线程数
        if (newConfig.getCorePoolSize() != getCorePoolSize()) {
            setCorePoolSize(newConfig.getCorePoolSize());
        }

        // 最大线程数
        if (newConfig.getMaximumPoolSize() != getMaximumPoolSize()) {
            setMaximumPoolSize(newConfig.getMaximumPoolSize());
        }

        // 队列容量
        if (newConfig.getQueueCapacity() != workQueue.getCapacity()) {
            workQueue.setCapacity(newConfig.getQueueCapacity());
        }
        // 拒绝策略
        if (newConfig.getRejectedExecutionHandler() != null &&
                !newConfig.getRejectedExecutionHandler().getClass().equals(
                        handler.getCurrentHandler().getClass())) {
            handler.setCurrentHandler(newConfig.getRejectedExecutionHandler());
        }
    }
}
