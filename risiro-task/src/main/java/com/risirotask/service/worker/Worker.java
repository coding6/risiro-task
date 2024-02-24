package com.risirotask.service.worker;

import com.risirotask.handler.TaskHandler;
import com.risirotask.handler.TaskRunnableHandler;
import com.risirotask.interfaces.TaskConfig;
import com.risirotask.service.data.TaskContext;
import com.risirotask.service.data.TaskState;
import com.risirotask.service.data.WorkerContext;
import com.risirotask.service.worker.interfaces.IWorker;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 执行本地队列任务的worker
 */
@Slf4j
public class Worker implements IWorker {

    private final TaskHandler<?> runner;

    private final Flux<TaskContext<?>> flux;

    private final Scheduler runScheduler;

    private final String workerName;

    private final int awaitTerminationSeconds;

    private Disposable disposable;

    public Worker(TaskHandler<?> runner, Flux<TaskContext<?>> flux, String workerName, Integer workerNum, int awaitTerminationSeconds) {
        this.runner = runner;
        this.flux = flux;
        this.runScheduler = Schedulers.newBoundedElastic(workerNum, 1024, workerName);
        this.workerName = workerName;
        this.awaitTerminationSeconds = awaitTerminationSeconds;
    }

    @Override
    public void start() {
        disposable = flux.parallel()
                .runOn(runScheduler)
                .flatMap(this::run)
                .flatMap(this::afterRun)
                .subscribe();
    }

    @Override
    public void stop() {
        if (disposable != null) {
            disposable.dispose(); // 取消订阅，停止接收新的任务
        }
        // 创建一个CountDownLatch以等待正在进行的任务
        CountDownLatch latch = new CountDownLatch(1);

        // 订阅flux流并在所有正在进行的任务完成时countDown
        flux.doOnComplete(latch::countDown).subscribe();

        try {
            // 等待10秒，如果任务完成则继续执行，否则超时
            if (!latch.await(awaitTerminationSeconds, TimeUnit.MILLISECONDS)) {
                System.out.println("Shutdown: Tasks did not finish within the timeout period.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 恢复中断状态 System.out.println("Shutdown interrupted.");
        } finally {
            if (!runScheduler.isDisposed()) {
                runScheduler.dispose(); // 强制关闭runScheduler
            }
        }
    }

    public <T> Mono<TaskContext<T>> run(TaskContext<T> taskContext) {
        System.out.println(workerName);
        WorkerContext workerContext = new WorkerContext(taskContext.getTaskInfo().getTaskId(), taskContext.getContext());
        if (runner instanceof TaskRunnableHandler) {
            ((TaskRunnableHandler<T>)runner).handler(taskContext.getTask(), workerContext);
        }
        return Mono.just(taskContext);
    }

    public <T> Mono<TaskContext<T>> afterRun(TaskContext<T> taskContext) {
        taskContext.getTaskInfo().setTaskState(TaskState.COMPLATE);
        return Mono.just(taskContext);
    }
}
