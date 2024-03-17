package com.risirotask.interfaces;

import com.risirotask.config.TaskProperties;
import com.risirotask.service.data.TaskContext;
import com.risirotask.service.data.TaskInfo;
import com.risirotask.service.data.TaskState;
import com.risirotask.service.listen.TaskListener;
import com.risirotask.service.submitter.TaskSubmitter;
import com.risirotask.util.SpringContextUtil;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * 任务处理器
 * @param <T>
 */
public class TaskProcessor<T> {

    private final TaskSubmitter taskSubmitter;

    private final T task;

    private final TaskProperties.TaskConfigProperties taskConfig;

    private final Map<String, Object> contexts = new HashMap<>();

    public TaskProcessor(TaskSubmitter taskSubmitter, T task, String taskName) {
        this.taskSubmitter = taskSubmitter;
        this.task = task;
        TaskProperties.TaskConfigProperties taskConfig = SpringContextUtil
                .getBean("tasks-com.risirotask.config.TaskProperties", TaskProperties.class)
                .getConfigByName(taskName);
        this.taskConfig = taskConfig;
    }

    public Mono<String> submit() {
        TaskContext<T> taskContext = new TaskContext<>(
                task,
                taskConfig,
                contexts,
                getTaskInfo()
        );
        return taskSubmitter.submit(taskContext)
                .doOnNext(taskId -> listenTask(taskId, taskContext.getTaskConfig().getRunningTimeout()));
    }

    public TaskProcessor<T> context(String key, Object val) {
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
