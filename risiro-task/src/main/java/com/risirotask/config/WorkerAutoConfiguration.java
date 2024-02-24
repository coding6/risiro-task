package com.risirotask.config;

import com.risirotask.service.worker.interfaces.IWorker;
import com.risirotask.service.worker.manager.WorkerManager;
import com.risirotask.util.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableConfigurationProperties({
        TaskProperties.class,
        RedisProperties.class
})
public class WorkerAutoConfiguration implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private TaskProperties taskProperties;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        SpringContextUtil.setAc(applicationReadyEvent.getApplicationContext());
        for (TaskProperties.TaskConfigProperties config : taskProperties.getConfigs()) {
            // 根据不同的 TaskType 创建不同类型的 Worker
            if (config.getType().equals(TaskProperties.TaskType.REDIS)) {
                WorkerManager workerManager = WorkerManager.getInstance();
                List<IWorker> redisWorkers = workerManager.createRedisWorkers(config);
                workerManager.start(redisWorkers);
            }
        }
    }
}
