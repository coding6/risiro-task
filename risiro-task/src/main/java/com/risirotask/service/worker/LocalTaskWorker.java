package com.risirotask.service.worker;

import com.risirotask.handler.TaskRunnableHandler;
import com.risirotask.interfaces.TaskConfig;
import com.risirotask.interfaces.data.TaskContext;
import com.risirotask.interfaces.data.WorkerContext;
import com.risirotask.service.listen.TaskListener;
import org.springframework.context.ApplicationContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class LocalTaskWorker implements Worker {

    private final ApplicationContext applicationContext;

    private final Flux<TaskContext<?, ?>> flux;

    public LocalTaskWorker(ApplicationContext applicationContext, Flux<TaskContext<?, ?>> flux) {
        this.applicationContext = applicationContext;
        this.flux = flux;
    }

    public void start() {
        flux.parallel(2)
                .runOn(Schedulers.newParallel("worker"))
                .flatMap(taskContext -> {
                    if (TaskListener.isExpired(taskContext.getTaskInfo().getTaskId())) {
                        System.out.println("过期了奥通知你一声");
                        return Mono.empty();
                    } else {
                        return Mono.fromRunnable(() -> run(taskContext))
                                .doOnSuccess(task -> {
                                    if (TaskListener.isExpired(taskContext.getTaskInfo().getTaskId())) {
                                        System.out.println("过期了奥通知你一声，任务跑完了，没你啥事别担心");
                                    }
                                });
                    }
                })
                .subscribe();
    }


    @Override
    @SuppressWarnings("unchecked")
    public <T, R extends TaskConfig> void run(TaskContext<T, R> taskContext) {
        R taskConfig = taskContext.getTaskConfig();
        String taskConsumerBeanName = taskConfig.getTaskConsumerBeanName();
        TaskRunnableHandler<T> runner = (TaskRunnableHandler<T>) applicationContext.getBean(taskConsumerBeanName);
        WorkerContext workerContext = new WorkerContext(taskContext.getTaskInfo().getTaskId(), taskContext.getContext());
        runner.handler(taskContext.getTask(), workerContext);
    }
}
