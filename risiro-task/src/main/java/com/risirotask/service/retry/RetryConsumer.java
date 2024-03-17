package com.risirotask.service.retry;

import com.lmax.disruptor.WorkHandler;
import com.risirotask.handler.TaskHandler;
import com.risirotask.handler.TaskRunnableHandler;
import com.risirotask.service.consumer.local_consumer.interfaces.TaskRunInterceptor;
import com.risirotask.service.data.TaskContext;
import com.risirotask.service.data.TaskState;
import com.risirotask.service.data.WorkerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @description 本地队列消费者，实现disruptor的WorkHandler进行消费
 * @author coding6
 */
@Slf4j
public class RetryConsumer implements WorkHandler<TaskContext<?>>, TaskRunInterceptor {

    @Override
    public void onEvent(TaskContext<?> taskContext) throws Exception {
        beforeRun(taskContext);
        run(taskContext);
        afterRun(taskContext);
    }

    @Override
    public <T> TaskContext<T> beforeRun(TaskContext<T> taskContext) {
        log.info("retry task is ready to run:{}", taskContext.getTaskInfo().getTaskId());
        return taskContext;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> TaskContext<T> run(TaskContext<T> taskContext) {
        WorkerContext workerContext = new WorkerContext(taskContext.getTaskInfo().getTaskId(), taskContext.getContext());
        TaskHandler<?> runner = taskContext.getRunner();
        try {
            if (runner instanceof TaskRunnableHandler) {
                ((TaskRunnableHandler<T>)runner).handler(taskContext.getTask(), workerContext);
            }
        } catch (Throwable e) {
            //如果重试遇到异常，直接更新任务状态
            taskContext.getTaskInfo().setTaskState(TaskState.FAILED);
            log.error("run task err:{}", e.getMessage());
            ((TaskRunnableHandler<T>)runner).fail(taskContext.getTask(), workerContext);
        }
        return taskContext;
    }

    @Override
    public <T> TaskContext<T> afterRun(TaskContext<T> taskContext) {
        return taskContext;
    }
}

