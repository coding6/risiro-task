package com.risirotask.core.metrics.config.data;

import lombok.Data;

/**
 * @author coding6
 * @create 2025/9/24
 * @description
 */
@Data
public class ThreadPoolJsonConfig {
    private int corePoolSize;
    private int maximumPoolSize;
    private int queueCapacity;
    private long keepAliveTime = 60L;
}
