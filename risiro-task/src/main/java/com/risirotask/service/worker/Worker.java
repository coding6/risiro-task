package com.risirotask.service.worker;

import com.risirotask.interfaces.TaskConfig;
import com.risirotask.interfaces.data.TaskContext;

public interface Worker {
    <T, R extends TaskConfig> void run(TaskContext<T, R> taskContext);
}
