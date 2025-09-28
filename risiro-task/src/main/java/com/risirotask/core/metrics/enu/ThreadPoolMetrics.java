package com.risirotask.core.metrics.enu;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author coding6
 * @create 2025/9/20
 * @description
 */
@AllArgsConstructor
@Getter
public enum ThreadPoolMetrics {
    ACTIVE_THREADS("active_threads", "活跃线程数"),
    TASK_COUNT("task_count", "任务总数"),
    COMPLETED_TASK_COUNT("completed_taskCount", "已完成任务数"),
     CORE_POOL_SIZE("core_pool_size", "核心线程数"),
    MAX_POOL_SIZE("max_pool_size", "最大线程数"),
    QUEUE_SIZE("queue_size", "队列大小"),
    QUEUE_CAPACITY("queue_capacity", "队列容量"),
    QUEUE_REMAINING_CAPACITY("queue_remaining_capacity", "队列剩余容量"),
    QUEUE_CAPACITY_PERCENT("queue_capacity_percent", "队列容量百分比");

    private final String code;

    private final String name;
}
