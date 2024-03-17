package com.risirotask.service.consumer.redis_consumer.interfaces;

import com.risirotask.config.TaskProperties;

import java.util.List;

public interface IWorkerManager {

    void stop();

    List<IWorker> createRedisWorkers(TaskProperties.TaskConfigProperties taskConfig);
}
