package com.risirotask.service.submitter;

import com.risirotask.interfaces.TaskProcessor;
import com.risirotask.interfaces.TaskConfig;
import com.risirotask.interfaces.data.TaskContext;
import com.risirotask.interfaces.data.TaskState;
import com.risirotask.service.worker.LocalTaskWorker;
import org.springframework.context.ApplicationContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 本地任务的提交者，使用本地队列
 */
public class LocalTaskSubmitter implements TaskSubmitter{

    private final Map<String, FluxSink<TaskContext<?, ?>>> fluxSinkMap;
    private final ApplicationContext applicationContext;
    private final AtomicLong taskIdGenerator = new AtomicLong(0);

    public LocalTaskSubmitter(ApplicationContext applicationContext) {
        this.fluxSinkMap = new ConcurrentHashMap<>();
        this.applicationContext = applicationContext;
    }

    public void init(String taskType) {
        if (fluxSinkMap.containsKey(taskType)) {
            return;
        }
        Flux<TaskContext<?, ?>> flux = Flux.create(fluxSink -> registerSink(taskType, fluxSink));
        LocalTaskWorker localTaskWorker = new LocalTaskWorker(applicationContext, flux);
        localTaskWorker.start();
    }

    @Override
    public <T, R extends TaskConfig> TaskProcessor<T, R> newTask(T task, R taskConfig) {
        return new TaskProcessor<>(this, task, taskConfig);
    }

    @Override
    public <T, R extends TaskConfig> Mono<String> asyncSubmit(TaskContext<T, R> taskContext) {
        R taskConfig = taskContext.getTaskConfig();
        init(taskConfig.getTaskConsumerBeanName());
        return Mono.just(taskContext)
                .map(trTaskContext -> {
                    String taskId = String.valueOf(taskIdGenerator.getAndIncrement());
                    //改变task状态
                    taskContext.getTaskInfo().setTaskState(TaskState.RUNNING);
                    taskContext.getTaskInfo().setTaskId(taskId);
                    String taskType = taskConfig.getTaskConsumerBeanName();
                    if (fluxSinkMap.containsKey(taskType)) {
                        fluxSinkMap.get(taskType).next(taskContext);
                    }
                    return taskId;
                });
    }

    public void registerSink(String taskType, FluxSink<TaskContext<?, ?>> fluxSink) {
        fluxSinkMap.put(taskType, fluxSink);
    }
}
