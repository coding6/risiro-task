package com.risirotask.service.consumer.local_consumer;

import com.risirotask.service.consumer.local_consumer.interfaces.ILocalExecutorManager;
import com.risirotask.service.consumer.local_consumer.queue.DisruptorQueue;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author coding6
 * @create 2024/3/17
 * @description
 */
@Slf4j
public class LocalExecutorManager implements ILocalExecutorManager {

    private static final LocalExecutorManager LOCAL_EXECUTOR_MANAGER = new LocalExecutorManager();

    private final Map<String, DisruptorQueue> disruptorQueueMap;

    public static LocalExecutorManager getInstance() {
        return LOCAL_EXECUTOR_MANAGER;
    }

    private LocalExecutorManager() {
        this.disruptorQueueMap = new ConcurrentHashMap<>();
    }

    @Override
    public void registeDisruptor(String taskName, DisruptorQueue disruptorQueue) {
        disruptorQueueMap.put(taskName, disruptorQueue);
    }

    @Override
    public boolean containsQueue(String taskName) {
        return disruptorQueueMap.containsKey(taskName);
    }

    @Override
    public DisruptorQueue get(String taskName) {
        return disruptorQueueMap.get(taskName);
    }

    @Override
    public void stop() {
        disruptorQueueMap.values().forEach(DisruptorQueue::stop);
    }
}
