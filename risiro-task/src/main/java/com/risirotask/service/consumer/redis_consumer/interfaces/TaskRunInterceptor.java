package com.risirotask.service.consumer.redis_consumer.interfaces;

import com.risirotask.service.data.TaskContext;
import reactor.core.publisher.Mono;

public interface TaskRunInterceptor {

    <T> Mono<TaskContext<T>> beforeRun(TaskContext<T> taskContext);

    <T> Mono<TaskContext<T>> run(TaskContext<T> taskContext);

    <T> Mono<TaskContext<T>> afterRun(TaskContext<T> taskContext);
}
