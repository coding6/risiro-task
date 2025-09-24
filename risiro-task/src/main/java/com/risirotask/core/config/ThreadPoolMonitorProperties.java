package com.risirotask.core.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * @author coding6
 * @create 2025/9/20
 * @description
 */
@ConfigurationProperties(prefix = "risiro-task.thread-pool.monitor")
@Getter
public class ThreadPoolMonitorProperties {
    public enum MonitorType {
        LOG, PROMETHEUS, CUSTOM
    }

    private MonitorType type = MonitorType.LOG; // 默认使用日志监控
    private CustomMonitorProperties custom;

    @Data
    public static class CustomMonitorProperties {
        private String reporterClass;
        private Map<String, String> params;
    }

}
