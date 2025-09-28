package com.risirotask.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author coding6
 * @create 2025/9/20
 * @description
 */
@Data
@ConfigurationProperties(prefix = "risiro-task.thread-pool.base")
public class ThreadPoolProperties {

    private boolean isForceChangeSize = false;

    private boolean waitForTasksToCompleteOnShutdown = true;

    private int awaitTerminationSeconds = 30;
}
