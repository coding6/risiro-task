package com.risirotask.service.submitter;

import com.risirotask.interfaces.TaskConfig;
import com.risirotask.interfaces.data.TaskContext;
import reactor.core.publisher.Mono;

public interface TaskSubmitter extends TaskProducer {

    <T, R extends TaskConfig> Mono<String> asyncSubmit(TaskContext<T, R> taskContext);
}
