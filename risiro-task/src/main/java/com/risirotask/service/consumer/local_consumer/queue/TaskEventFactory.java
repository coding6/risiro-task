package com.risirotask.service.consumer.local_consumer.queue;

import com.lmax.disruptor.EventFactory;
import com.risirotask.service.data.TaskContext;

public class TaskEventFactory implements EventFactory<TaskContext<?>> {
    @Override
    public TaskContext<?> newInstance() {
        return new TaskContext<>();
    }
}
