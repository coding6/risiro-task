package com.risirotask.core.config;

import com.risirotask.core.queue.DynamicLinkedBlockingQueue;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.*;

/**
 * @author coding6
 * @create 2025/9/20
 * @description
 */
@Getter
@Setter
public class ThreadPoolConfig {
    private String poolName;
    private int corePoolSize;
    private int maximumPoolSize;
    private long keepAliveTime;
    private TimeUnit unit;
    private int queueCapacity;
    private RejectedExecutionHandler rejectedExecutionHandler;
    private ThreadFactory threadFactory;

    // 构造方法
    public ThreadPoolConfig(String poolName, int corePoolSize, int maximumPoolSize,
                            long keepAliveTime, TimeUnit unit,
                            int queueCapacity,
                            RejectedExecutionHandler rejectedExecutionHandler) {
        this.corePoolSize = corePoolSize;
        this.poolName =  poolName;
        this.maximumPoolSize = maximumPoolSize;
        this.keepAliveTime = keepAliveTime;
        this.unit = unit;
        this.queueCapacity = queueCapacity;
        this.rejectedExecutionHandler = rejectedExecutionHandler;
        this.threadFactory = Executors.defaultThreadFactory();
    }

    public static ThreadPoolConfig fromExecutor(String poolName, ThreadPoolExecutor executor) {
        BlockingQueue<Runnable> queue = executor.getQueue();
        int queueCapacity = queue instanceof DynamicLinkedBlockingQueue ?
                ((DynamicLinkedBlockingQueue<Runnable>) queue).getCapacity() : queue.size() + queue.remainingCapacity();

        return new ThreadPoolConfig(poolName,
                executor.getCorePoolSize(),
                executor.getMaximumPoolSize(),
                executor.getKeepAliveTime(TimeUnit.SECONDS),
                TimeUnit.SECONDS,
                queueCapacity,
                executor.getRejectedExecutionHandler()
        );
    }
}
