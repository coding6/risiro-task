package com.risirotask.service.storage;

import com.risirotask.config.TaskProperties;
import com.risirotask.util.SpringContextUtil;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import lombok.Getter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Getter
public class RedisStorage {

    public static final RedisStorage REDIS_STORAGE = new RedisStorage();

    private static final String TASKS_STREAM_KEY = "tasks-stream";
    private final RedisReactiveCommands<String, String> redisCommands;

    private RedisStorage() {
        SpringContextUtil
                .getBean("tasks-com.risirotask.config.TaskProperties", TaskProperties.class)
                .getRedis()
        String redisUrl = redisProperties.getUrl();
        RedisClient redisClient = RedisClient.create(redisUrl);
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        this.redisCommands = connection.reactive();
    }

    public static RedisStorage getInstance() {
        return REDIS_STORAGE;
    }

    public Mono<Void> lpush(String key, String value) {
        return redisCommands
                .lpush(key, value)
                .then();
    }

    public Flux<String> rpop(String queueKey) {
        return redisCommands.rpop(queueKey, 1L)
                .filter(Objects::nonNull) // 确保消息不为空
                .map(message -> {
                    // 在这里处理消息，例如解析 JSON
                    // 假设 message 是一个 JSON 字符串，你需要将其解析为对应的对象或 Map
                    // 这里只是简单地返回原始字符串
                    return message;
                });
    }

}
