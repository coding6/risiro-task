package com.risirotask.service.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.risirotask.constant.Constant;
import com.risirotask.handler.TaskHandler;
import com.risirotask.handler.TaskRunnableHandler;
import com.risirotask.interfaces.TaskConfig;
import com.risirotask.service.data.TaskContext;
import com.risirotask.service.data.TaskState;
import com.risirotask.service.data.WorkerContext;
import com.risirotask.service.storage.RedisStorage;
import com.risirotask.service.worker.interfaces.IWorker;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 执行redis队列任务的worker
 * @param <T>
 */
@Slf4j
public class RedisWorker<T> implements IWorker {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final TaskHandler<T> runner;

    private final RedisStorage redisStorage;

    private Disposable disposable;

    private final Scheduler runScheduler;

    private final AtomicBoolean isRunning;

    private final String taskName;


    public RedisWorker(TaskHandler<T> runner,
                       RedisStorage redisStorage,
                       String workerName,
                       String taskName) {
        this.runner = runner;
        this.redisStorage = redisStorage;
        this.runScheduler = Schedulers.newSingle(workerName);
        this.isRunning = new AtomicBoolean(false);
        this.taskName = taskName;
    }

    private Mono<TaskContext<?>> apply(String val) {
        try {
            TaskContext<?> taskContext = MAPPER.readValue(val, TaskContext.class);
            String dataType = taskContext.getTaskConfig().getDataType();
            Class<?> taskClass = Class.forName(dataType);
            JavaType taskType = MAPPER.getTypeFactory().constructParametricType(TaskContext.class, taskClass);
            taskContext = MAPPER.readValue(val, taskType);
            return Mono.just(taskContext);
        } catch (JsonProcessingException e) {
            return Mono.empty();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void start() {
        isRunning.set(true);
        disposable = Flux.defer(() -> Mono.delay(Duration.ofMillis(100), runScheduler)
                        .flatMap(t -> redisStorage.rpop(Constant.QUEUE_KEY_PREFIX + taskName).next())
                        .publishOn(runScheduler)
                        .filter(Objects::nonNull))
                .repeat(isRunning::get)
                .flatMap(this::apply)
                .flatMap(this::run)
                .flatMap(this::afterRun)
                .onErrorContinue((throwable, element) -> {
                    // 处理异常，例如打印日志
                    log.error("An error occurred in the stream while processing element: {}", element, throwable);
                })
                .subscribe();
        log.info("worker started:{}", taskName);
    }

    @Override
    public void stop() {
        // 设置停止标志
        isRunning.set(false);

        // 取消订阅，停止接收新任务
        if (disposable != null) {
            disposable.dispose();
        }

        // 等待任务完成，或者在10秒后强制结束
        // 所有任务都应该在这里结束
        Flux.interval(Duration.ofSeconds(1))
                .take(10)
                .filter(tick -> !isRunning.get()) // 如果已经停止运行，则跳过等待
                .subscribe(null, null, runScheduler::dispose);
    }

    @SuppressWarnings("unchecked")
    public <T> Mono<TaskContext<T>> run(TaskContext<T> taskContext) {
        WorkerContext workerContext = new WorkerContext(taskContext.getTaskInfo().getTaskId(), taskContext.getContext());
        if (runner instanceof TaskRunnableHandler) {
            ((TaskRunnableHandler<T>)runner).handler(taskContext.getTask(), workerContext);
        }
        System.out.println(Thread.currentThread().getName() + ":" + taskContext.getTaskInfo().getTaskState());
        return Mono.just(taskContext);
    }

    public <T> Mono<TaskContext<T>> afterRun(TaskContext<T> taskContext) {
        taskContext.getTaskInfo().setTaskState(TaskState.COMPLATE);
        System.out.println(Thread.currentThread().getName() + ":" + taskContext.getTaskInfo().getTaskState());
        return Mono.just(taskContext);
    }
}
