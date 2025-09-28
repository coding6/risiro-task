package com.risirotask.core.metrics.config;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.risirotask.config.ThreadPoolConfig;
import com.risirotask.core.metrics.config.data.ThreadPoolJsonConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

/**
 * @author coding6
 * @create 2025/9/24
 * @description
 */
@Slf4j
public class ApolloConfigCenter implements ConfigCenter {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final Config config;

    public ApolloConfigCenter(String namespace) {
        this.config = ConfigService.getConfig(namespace);
    }

    @Override
    public void watch(String poolName, Consumer<ThreadPoolConfig> callBack) {
        //初始化配置，将apollo的配置值作为线程池启动的默认值
        updateConfig(poolName, callBack);
        config.addChangeListener(new ConfigChangeListener() {
            @Override
            public void onChange(ConfigChangeEvent changeEvent) {
                if (changeEvent.isChanged(poolName)) {
                    updateConfig(poolName, callBack);
                }
            }
        });
    }

    @Override
    public void getConfig() {

    }

    public void updateConfig(String poolName, Consumer<ThreadPoolConfig> callBack) {
        String json = config.getProperty(poolName,  "{}");
        try {
            // 如果配置为空，使用默认配置
            if (json == null || json.equals("{}")) {
                log.warn("No configuration found for pool in apollo: {}, using default configuration", poolName);
                return;
            }
            
            ThreadPoolJsonConfig threadPoolJsonConfig = MAPPER.readValue(json, ThreadPoolJsonConfig.class);
            ThreadPoolConfig threadPoolConfig = new ThreadPoolConfig(threadPoolJsonConfig);
            callBack.accept(threadPoolConfig);
        } catch (JsonProcessingException e) {
            log.error("poolName={} updateConfig error", poolName, e);
        }
    }

}
