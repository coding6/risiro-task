package com.risirotask.service.retry;

import com.risirotask.service.data.TaskContext;
import com.risirotask.service.consumer.local_consumer.queue.DisruptorQueue;

/**
 * 任务重试处理逻辑
 */
public class Retry implements IRetry {

    private static final Retry RETRY = new Retry();
    private final DisruptorQueue retryQueue;

    private Retry() {
        retryQueue = new DisruptorQueue(1, new RetryConsumer());
        retryQueue.start();
    }

    public static Retry getInstance() {
        return RETRY;
    }

    /**
     * 向重试队列中提交一个任务
     * @param taskContext
     * @param <T>
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> void retry(TaskContext<T> taskContext) {
        System.out.println("重试队列中已发送一个任务:" + taskContext.getTaskInfo().getTaskId());
        retryQueue.put(taskContext);
    }

    @Override
    public <T> void retryWhen(TaskContext<T> taskContext, Throwable e) {

    }
}
