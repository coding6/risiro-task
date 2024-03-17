package com.risirotask.service.consumer.local_consumer.interfaces;

import com.risirotask.service.data.TaskContext;

public interface TaskRunInterceptor {

    <T> TaskContext<T> beforeRun(TaskContext<T> taskContext);

    <T> TaskContext<T> run(TaskContext<T> taskContext);

    <T> TaskContext<T> afterRun(TaskContext<T> taskContext);
}
