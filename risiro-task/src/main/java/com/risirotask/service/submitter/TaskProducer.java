package com.risirotask.service.submitter;

import com.risirotask.interfaces.TaskProcessor;
import com.risirotask.interfaces.TaskConfig;

public interface TaskProducer {

    <T> TaskProcessor<T> newTask(T task, String taskName);
}
