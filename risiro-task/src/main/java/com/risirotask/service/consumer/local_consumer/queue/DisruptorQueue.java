package com.risirotask.service.consumer.local_consumer.queue;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.ProducerType;
import com.risirotask.service.consumer.local_consumer.LocalConsumer;
import com.risirotask.service.consumer.local_consumer.queue.interfaces.IDisruptorQueue;
import com.risirotask.service.data.TaskContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @description 定义并封装Disruptor的操作
 * @author coding6
 */
@Getter
@Slf4j
public class DisruptorQueue implements IDisruptorQueue{

    private static final int BUFFER_SIZE = 1024;

    private final RingBuffer<TaskContext<?>> ringBuffer;

    private final ExecutorService executor;

    private final WorkerPool<TaskContext<?>> workerPool;

    private final boolean waitForTasksToCompleteOnShutdown = false;

    private final int awaitTerminationSeconds = 0;

    private final AtomicBoolean isRunning = new AtomicBoolean(true);

    /**
     * @description
     * @author coding6
     */
    public DisruptorQueue(int threadNum) {
        executor = Executors.newFixedThreadPool(threadNum);
        EventFactory<TaskContext<?>> factory = new TaskEventFactory();

        //1 创建RingBuffer
        ringBuffer =
                RingBuffer.create(ProducerType.MULTI,
                        factory,
                        BUFFER_SIZE,
                        new YieldingWaitStrategy());
        //2 通过ringBuffer 创建一个屏障
        SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();

        //3 创建消费者的数组:
        LocalConsumer[] consumers = new LocalConsumer[threadNum];
        for(int i = 0; i < consumers.length; i++) {
            consumers[i] = new LocalConsumer();
        }

        //4 构建多消费者工作池
        workerPool = new WorkerPool<>(
                ringBuffer,
                sequenceBarrier,
                new EventExceptionHandler(),
                consumers);

        //5 设置多个消费者的sequence序号 用于单独统计消费进度, 并且设置到ringbuffer中
        ringBuffer.addGatingSequences(workerPool.getWorkerSequences());
    }

    public DisruptorQueue(int threadNum, WorkHandler<TaskContext<?>> consumer) {
        executor = Executors.newFixedThreadPool(threadNum);
        EventFactory<TaskContext<?>> factory = new TaskEventFactory();

        //1 创建RingBuffer
        ringBuffer =
                RingBuffer.create(ProducerType.MULTI,
                        factory,
                        BUFFER_SIZE,
                        new YieldingWaitStrategy());
        //2 通过ringBuffer 创建一个屏障
        SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();

        //3 构建多消费者工作池
        workerPool = new WorkerPool<>(
                ringBuffer,
                sequenceBarrier,
                new EventExceptionHandler(),
                consumer);

        //4 设置多个消费者的sequence序号 用于单独统计消费进度, 并且设置到ringbuffer中
        ringBuffer.addGatingSequences(workerPool.getWorkerSequences());
    }

    @Override
    public void start() {
        //6 启动workerPool
        workerPool.start(executor);
    }

    @Override
    public void stop() {
        log.info("优雅关闭开始");
        isRunning.set(false);
        //优雅关闭workerpool
        workerPool.drainAndHalt();
        //优雅关闭线程池
        if (this.waitForTasksToCompleteOnShutdown) {
            this.executor.shutdown();
        } else {
            this.executor.shutdownNow();
        }
        awaitTerminationIfNecessary();
        log.info("优雅关闭结束");
    }

    private void awaitTerminationIfNecessary() {
        if (awaitTerminationSeconds > 0) {
            try {
                executor.awaitTermination(awaitTerminationSeconds, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void put(TaskContext<T> taskContext) {
        if (!isRunning.get()) {
            return;
        }
        long sequence = ringBuffer.next();
        try {
            TaskContext<T> task = (TaskContext<T>) ringBuffer.get(sequence);
            task.setContext(taskContext.getContext());
            task.setTask(taskContext.getTask());
            task.setTaskConfig(taskContext.getTaskConfig());
            task.setTaskInfo(taskContext.getTaskInfo());
            task.setRunner(taskContext.getRunner());
            System.out.println("已发送一个任务:" + task.getTaskInfo().getTaskId());
        } finally {
            ringBuffer.publish(sequence);
        }
    }


    static class EventExceptionHandler implements ExceptionHandler<TaskContext<?>> {

        public void handleEventException(Throwable ex, long sequence, TaskContext<?> event) {
        }

        public void handleOnStartException(Throwable ex) {
        }

        public void handleOnShutdownException(Throwable ex) {
        }

    }
}
