package com.risirotask.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "tasks")
public class TaskProperties {

    private List<TaskConfigProperties> configs;

    public RedisConfigProperties redis;

    @Data
    public static class TaskConfigProperties {
        private String dataType;
        private String taskName;
        private String consumerBeanName;
        private long runningTimeout;
        private long retryTimes;
        private int workerNum;
        private TaskType type;
        private Class<?> retryWhenErr;
    }

    @Data
    public static class RedisConfigProperties {
        private String url;
        private Boolean enableWorker;
    }

    @Getter
    public enum TaskType {
        LOCAL,
        REDIS,
        KAFKA
    }

    public TaskConfigProperties getConfigByName(String taskName) {
        return configs.stream()
                .filter(taskConfigProperties -> taskConfigProperties.getTaskName().equals(taskName))
                .findAny()
                .get();
    }
}
