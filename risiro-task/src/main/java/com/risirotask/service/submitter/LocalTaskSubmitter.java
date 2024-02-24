package com.risirotask.service.submitter;

import com.risirotask.config.TaskProperties;
import com.risirotask.interfaces.TaskProcessor;
import com.risirotask.interfaces.TaskConfig;
import com.risirotask.service.data.TaskContext;
import com.risirotask.service.data.TaskState;
import com.risirotask.service.worker.interfaces.IWorker;
import com.risirotask.service.worker.manager.WorkerManager;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 本地任务的提交者，使用本地队列
 */
public class LocalTaskSubmitter implements TaskSubmitter{

    private final Map<String, FluxSink<TaskContext<?>>> fluxSinkMap;
    private final AtomicLong taskIdGenerator = new AtomicLong(0);

    public LocalTaskSubmitter() {
        this.fluxSinkMap = new ConcurrentHashMap<>();
    }

    public void init(TaskProperties.TaskConfigProperties taskConfig) {
        if (fluxSinkMap.containsKey(taskConfig.getConsumerBeanName())) {
            return;
        }
        Flux<TaskContext<?>> flux = Flux.<TaskContext<?>>create(fluxSink -> {
                    registerSink(taskConfig.getConsumerBeanName(), fluxSink);
                });
//                .publish()    // 将Flux转换为"热"序列
//                .autoConnect();
        WorkerManager workerManager = WorkerManager.getInstance();
        List<IWorker> workers = workerManager.createWorkers(taskConfig, flux);
        workerManager.start(workers);
    }

    @Override
    public <T> TaskProcessor<T> newTask(T task, String taskName) {
        return new TaskProcessor<>(this, task, taskName);
    }

    @Override
    public <T> Mono<String> asyncSubmit(TaskContext<T> taskContext) {
        TaskProperties.TaskConfigProperties taskConfig = taskContext.getTaskConfig();
        init(taskConfig);
        return Mono.just(taskContext)
                .map(trTaskContext -> {
                    String taskId = String.valueOf(taskIdGenerator.getAndIncrement());
                    //改变task状态
                    taskContext.getTaskInfo().setTaskState(TaskState.RUNNING);
                    taskContext.getTaskInfo().setTaskId(taskId);
                    String taskType = taskConfig.getConsumerBeanName();
                    if (fluxSinkMap.containsKey(taskType)) {
                        fluxSinkMap.get(taskType).next(taskContext);
                    }
                    return taskId;
                });
    }

    public void registerSink(String taskType, FluxSink<TaskContext<?>> fluxSink) {
        fluxSinkMap.put(taskType, fluxSink);
    }
}
