package com.risirotask.interfaces.data;

import com.risirotask.interfaces.TaskConfig;
import com.risirotask.service.submitter.TaskSubmitter;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class TaskContext<T, R extends TaskConfig> {

    private TaskSubmitter taskSubmitter;

    private T task;

    private R taskConfig;

    private TaskInfo taskInfo;

    private Map<String, Object> context;

    public TaskContext(TaskSubmitter taskSubmitter, T task, R taskConfig, Map<String, Object> context, TaskInfo taskInfo) {
        this.taskSubmitter = taskSubmitter;
        this.task = task;
        this.taskConfig = taskConfig;
        this.context = context;
        this.taskInfo = taskInfo;
    }
}
