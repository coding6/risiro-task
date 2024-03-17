package com.risirotask.service.retry;

import com.risirotask.service.data.TaskContext;

/**
 * @author coding6
 * @create 2024/3/16
 * @description retry接口，定义retry的规范
 */
public interface IRetry {

    /**
     * @description
     * @author coding6
     */
    <T> void retry(TaskContext<T> taskContext);

    /**
     * @description
     * @author coding6
     */
    <T> void retryWhen(TaskContext<T> taskContext, Throwable e);
}
