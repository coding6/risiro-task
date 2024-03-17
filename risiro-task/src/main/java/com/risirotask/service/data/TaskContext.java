package com.risirotask.service.data;

import com.risirotask.config.TaskProperties;
import com.risirotask.handler.TaskHandler;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class TaskContext<T> {

    private T task;

    private TaskProperties.TaskConfigProperties taskConfig;

    private TaskInfo taskInfo;

    private Map<String, Object> context;

    private TaskHandler<?> runner;

    public TaskContext(T task, TaskProperties.TaskConfigProperties taskConfig, Map<String, Object> context, TaskInfo taskInfo) {
        this.task = task;
        this.taskConfig = taskConfig;
        this.context = context;
        this.taskInfo = taskInfo;
    }

    public TaskContext(T task, TaskProperties.TaskConfigProperties taskConfig, Map<String, Object> context, TaskInfo taskInfo, TaskHandler<?> runner) {
        this.task = task;
        this.taskConfig = taskConfig;
        this.context = context;
        this.taskInfo = taskInfo;
        this.runner = runner;
    }
}
