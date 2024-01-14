package com.risirotask.interfaces;

import com.risirotask.interfaces.data.TaskContext;
import com.risirotask.interfaces.data.TaskInfo;
import com.risirotask.interfaces.data.TaskState;
import com.risirotask.service.listen.TaskListener;
import com.risirotask.service.submitter.TaskSubmitter;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

public class TaskProcessor<T, R extends TaskConfig> {

    private final TaskSubmitter taskSubmitter;

    private final T task;

    private final R taskConfig;


    private final Map<String, Object> contexts = new HashMap<>();

    public TaskProcessor(TaskSubmitter taskSubmitter, T task, R taskConfig) {
        this.taskSubmitter = taskSubmitter;
        this.task = task;
        this.taskConfig = taskConfig;
    }

    public Mono<String> asyncSubmit() {
        TaskContext<T, R> taskContext = new TaskContext<>(
                taskSubmitter,
                task,
                taskConfig,
                contexts,
                getTaskInfo()
        );
        return taskSubmitter.asyncSubmit(taskContext)
                .doOnNext(taskId -> listenTask(taskId, taskContext.getTaskConfig().getRunningTimeout()));
    }

    public TaskProcessor<T, R> context(String key, Object val) {
        contexts.put(key, val);
        return this;
    }

    public TaskInfo getTaskInfo() {
        return TaskInfo.builder()
                .taskState(TaskState.PENDING)
                .build();
    }

    private void listenTask(String taskId, Long expire) {
        TaskListener.add(taskId, expire);
    }
}
