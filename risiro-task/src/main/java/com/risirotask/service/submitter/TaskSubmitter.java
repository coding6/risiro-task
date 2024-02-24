package com.risirotask.service.submitter;

import com.risirotask.interfaces.TaskConfig;
import com.risirotask.service.data.TaskContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

public interface TaskSubmitter extends TaskProducer {

    <T> Mono<String> asyncSubmit(TaskContext<T> taskContext);


}
