package com.risirotask.service.listen;

import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;

import java.util.concurrent.TimeUnit;

public class TaskListener {
    public static final ExpiringMap<String, String> TASK_DELAY_MAP = ExpiringMap.builder().variableExpiration()
            .expirationListener((thekey, thevalue) -> {
                System.out.println("key: " + thekey + " -- value: " + thevalue + " 过期");
            }).build();

    public static void add(String taskId, Long expire) {
        TASK_DELAY_MAP.put(taskId, taskId, ExpirationPolicy.CREATED, expire, TimeUnit.SECONDS);
    }

    public static boolean isExpired(String taskId) {
        return !TASK_DELAY_MAP.containsKey(taskId);
    }
}
