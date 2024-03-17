package com.risirotask.service.submitter;

import com.risirotask.config.TaskProperties;
import com.risirotask.handler.TaskHandler;
import com.risirotask.interfaces.TaskProcessor;
import com.risirotask.service.consumer.local_consumer.LocalExecutorManager;
import com.risirotask.service.data.TaskContext;
import com.risirotask.service.data.TaskState;
import com.risirotask.service.consumer.local_consumer.queue.DisruptorQueue;
import com.risirotask.util.SpringContextUtil;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author coding6
 * 本地任务的提交类，提交的任务会放入内存队列
 */
public class LocalTaskSubmitter implements TaskSubmitter{

    private final AtomicLong taskIdGenerator = new AtomicLong(0);

    public LocalTaskSubmitter() {}

    public void init(TaskProperties.TaskConfigProperties taskConfig) {
        if (LocalExecutorManager.getInstance().containsQueue(taskConfig.getConsumerBeanName())) {
            return;
        }
        DisruptorQueue disruptorQueue = new DisruptorQueue(taskConfig.getWorkerNum());
        LocalExecutorManager.getInstance().registeDisruptor(taskConfig.getConsumerBeanName(), disruptorQueue);
        disruptorQueue.start();
    }

    @Override
    public <T> TaskProcessor<T> newTask(T task, String taskName) {
        return new TaskProcessor<>(this, task, taskName);
    }

    @Override
    public <T> Mono<String> submit(TaskContext<T> taskContext) {
        TaskProperties.TaskConfigProperties taskConfig = taskContext.getTaskConfig();
        TaskHandler<?> handler = SpringContextUtil.getBean(taskConfig.getConsumerBeanName(), TaskHandler.class);
        taskContext.setRunner(handler);
        init(taskConfig);
        String taskId = String.valueOf(taskIdGenerator.getAndIncrement());
        taskContext.getTaskInfo().setTaskState(TaskState.RUNNING);
        taskContext.getTaskInfo().setTaskId(taskId);
        String taskType = taskConfig.getConsumerBeanName();
        if (LocalExecutorManager.getInstance().containsQueue(taskType)) {
            LocalExecutorManager.getInstance().get(taskType).put(taskContext);
        }
        return Mono.just(taskId);
    }
}
