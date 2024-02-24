package com.risirotask.service.worker.interfaces;

import com.risirotask.config.TaskProperties;
import com.risirotask.interfaces.TaskConfig;
import com.risirotask.service.data.TaskContext;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import reactor.core.publisher.Flux;

import java.util.List;

public interface IWorkerManager {

    void start(List<IWorker> workers);

    void stop(List<IWorker> workers);

    List<IWorker> createWorkers(TaskProperties.TaskConfigProperties taskConfig, Flux<TaskContext<?>> flux);

    List<IWorker> createRedisWorkers(TaskProperties.TaskConfigProperties taskConfig);
}
