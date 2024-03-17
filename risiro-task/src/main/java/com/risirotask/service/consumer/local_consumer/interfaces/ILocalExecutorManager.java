package com.risirotask.service.consumer.local_consumer.interfaces;

import com.risirotask.service.consumer.local_consumer.queue.DisruptorQueue;

/**
 * @author coding6
 * @create 2024/3/16
 * @description 执行器管理器，管理队列和线程的生命周期
 */
public interface ILocalExecutorManager {

    void registeDisruptor(String taskName, DisruptorQueue disruptorQueue);

    boolean containsQueue(String taskName);

    DisruptorQueue get(String taskName);

    void stop();

}
