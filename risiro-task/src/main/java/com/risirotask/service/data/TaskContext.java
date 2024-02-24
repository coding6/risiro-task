package com.risirotask.service.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.risirotask.config.TaskProperties;
import com.risirotask.interfaces.TaskConfig;
import com.risirotask.service.submitter.TaskSubmitter;
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



    public TaskContext(T task, TaskProperties.TaskConfigProperties taskConfig, Map<String, Object> context, TaskInfo taskInfo) {
        this.task = task;
        this.taskConfig = taskConfig;
        this.context = context;
        this.taskInfo = taskInfo;
    }
}
