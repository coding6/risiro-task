package com.risirotask.interfaces;

import java.time.Duration;

/**
 * 任务抽象类，使用者需要继承这个
 */
public interface TaskConfig {

    long DEFAULT_RUNNING_TIMEOUT = 10000L;

    int MAX_RETRY_TIMES = 3;

    String getTaskConsumerBeanName();

    default long getRunningTimeout() {
        return DEFAULT_RUNNING_TIMEOUT;
    }

    default long getRetryTimes() {
        return MAX_RETRY_TIMES;
    }

}
