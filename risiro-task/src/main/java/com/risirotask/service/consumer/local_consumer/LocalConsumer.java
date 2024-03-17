package com.risirotask.service.consumer.local_consumer;

import com.lmax.disruptor.WorkHandler;
import com.risirotask.handler.TaskHandler;
import com.risirotask.handler.TaskRunnableHandler;
import com.risirotask.service.consumer.local_consumer.interfaces.TaskRunInterceptor;
import com.risirotask.service.data.TaskContext;
import com.risirotask.service.data.TaskState;
import com.risirotask.service.data.WorkerContext;
import com.risirotask.service.retry.Retry;
import lombok.extern.slf4j.Slf4j;

/**
 * @description 本地队列消费者，实现disruptor的WorkHandler进行消费
 * @author coding6
 */
@Slf4j
public class LocalConsumer implements WorkHandler<TaskContext<?>>, TaskRunInterceptor {

    @Override
    public void onEvent(TaskContext<?> taskContext) throws Exception {
        beforeRun(taskContext);
        run(taskContext);
        afterRun(taskContext);
    }

    @Override
    public <T> TaskContext<T> beforeRun(TaskContext<T> taskContext) {
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
            taskContext.getTaskInfo().setTaskState(TaskState.FAILED);
            log.error("run task err:{}", e.getMessage());
            if (taskContext.getTaskConfig().getRetryTimes() > 0) {
                //任务进入重试队列
                Retry.getInstance().retry(taskContext);

            } else {
                //任务执行fail方法
                ((TaskRunnableHandler<T>)runner).fail(taskContext.getTask(), workerContext);
            }
        }
        return taskContext;
    }

    @Override
    public <T> TaskContext<T> afterRun(TaskContext<T> taskContext) {
        //如果任务失败了做什么
        //如果任务成功了做什么
        //如果超时了做什么
        return taskContext;
    }
}
