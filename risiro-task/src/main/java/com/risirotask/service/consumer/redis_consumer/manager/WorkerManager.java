package com.risirotask.service.consumer.redis_consumer.manager;

import com.risirotask.config.TaskProperties;
import com.risirotask.service.consumer.redis_consumer.RedisWorker;
import com.risirotask.service.storage.RedisStorage;
import com.risirotask.util.SpringContextUtil;
import com.risirotask.handler.TaskHandler;
import com.risirotask.service.consumer.redis_consumer.interfaces.IWorker;
import com.risirotask.service.consumer.redis_consumer.interfaces.IWorkerManager;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class WorkerManager implements IWorkerManager{
    private static final WorkerManager WORKER_MANAGER = new WorkerManager();

    private WorkerManager() {

    }

    public static WorkerManager getInstance() {
        return WORKER_MANAGER;
    }

    public static final ConcurrentHashMap<String, List<IWorker>> ALL_WORKERS = new ConcurrentHashMap<>(1);


    @Override
    public List<IWorker> createRedisWorkers(TaskProperties.TaskConfigProperties taskConfig) {
        TaskHandler<?> handler = SpringContextUtil.getBean(taskConfig.getConsumerBeanName(), TaskHandler.class);
        List<IWorker> workers = IntStream.range(0, taskConfig.getWorkerNum())
                .mapToObj(i ->
                        new RedisWorker<>(handler, RedisStorage.getInstance(), String.format("worker-redis-%s-%d",
                                taskConfig.getConsumerBeanName(), i),
                                taskConfig.getConsumerBeanName()))
                .collect(Collectors.toList());
        ALL_WORKERS.put(taskConfig.getConsumerBeanName(), workers);
        return workers;
    }

    @Override
    public void stop() {
        List<IWorker> allWorkers = ALL_WORKERS.values()
                .stream()
                .flatMap(List::stream)
                .toList();
        allWorkers.forEach(IWorker::stop);
    }
}
