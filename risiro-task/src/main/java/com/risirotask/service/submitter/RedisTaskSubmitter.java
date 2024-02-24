package com.risirotask.service.submitter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.risirotask.config.TaskProperties;
import com.risirotask.constant.Constant;
import com.risirotask.interfaces.TaskConfig;
import com.risirotask.interfaces.TaskProcessor;
import com.risirotask.service.data.TaskContext;
import com.risirotask.service.storage.RedisStorage;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import io.lettuce.core.codec.ByteArrayCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class RedisTaskSubmitter implements TaskSubmitter {

    @Override
    public <T> TaskProcessor<T> newTask(T task, String taskName) {
        return new TaskProcessor<>(this, task, taskName);
    }

    @Override
    public <T> Mono<String> asyncSubmit(TaskContext<T> taskContext) {
        return generateTaskId()
                .flatMap(taskId -> {
                    try {
                        String serializedTask = serializeTask(taskContext);
                        return validateTask(serializedTask)
                                .filter(Boolean::booleanValue)
                                .switchIfEmpty(Mono.error(new RuntimeException("Task validation failed")))
                                .then(pushToRedis(taskContext.getTaskConfig().getConsumerBeanName(), serializedTask))
                                .thenReturn(taskId);
                    } catch (JsonProcessingException e) {
                        return Mono.error(e);
                    }
                });
    }

    private <T> String serializeTask(T task) throws JsonProcessingException {
        // 序列化逻辑
        // 例如使用JSON序列化
        return new ObjectMapper().writeValueAsString(task);
    }

    private Mono<String> generateTaskId() {
        // Generate a globally unique taskId
        return Mono.just(UUID.randomUUID().toString());
    }

    private Mono<Boolean> validateTask(String taskData) {
        // Implement the task validation logic
        // Return Mono<Boolean>
        return Mono.just(true); // Placeholder for actual validation logic
    }

    private Mono<Void> pushToRedis(String consumerName, String taskData) {
        return RedisStorage.getInstance().lpush(Constant.QUEUE_KEY_PREFIX + consumerName, taskData);
    }
}
