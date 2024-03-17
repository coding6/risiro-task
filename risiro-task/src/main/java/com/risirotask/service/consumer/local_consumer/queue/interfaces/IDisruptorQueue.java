package com.risirotask.service.consumer.local_consumer.queue.interfaces;

import com.risirotask.service.data.TaskContext;

/**
 * @author coding6
 * @create 2024/3/16
 * @description
 */
public interface IDisruptorQueue {

    /**
     * @description 向queue提交一个任务
     * @author coding6
     */
    <T> void put(TaskContext<T> taskContext);

    void start();

    void stop();
}
