package com.risirotask.handler;

import com.risirotask.exception.TaskException;
import com.risirotask.service.data.WorkerContext;

/**
 * @description 实现该接口的handler，自定义任务的处理逻辑
 * @param <T>
 */
@FunctionalInterface
public interface TaskRunnableHandler<T> extends TaskHandler<T> {

    /**
     * 业务方实现这个方法，自定义任务的处理逻辑
     * @param taskRequest 业务方传入的任务体
     * @return 无返回值，RunnableHandler接口不提供任务result的存储
     */
    void handler(T taskRequest, WorkerContext workerContext) throws TaskException;

}
