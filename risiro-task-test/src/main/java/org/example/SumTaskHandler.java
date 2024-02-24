package org.example;

import com.risirotask.handler.TaskRunnableHandler;
import com.risirotask.service.data.WorkerContext;
import org.springframework.stereotype.Component;

@Component("sumTask")
public class SumTaskHandler implements TaskRunnableHandler<SumTask> {

    @Override
    public void handler(SumTask taskRequest, WorkerContext workerContext) {
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        Object val = workerContext.getContext().get("key1");
        System.out.println("sumTask收到回调：" + taskRequest.getA() + ":" + taskRequest.getB() + ":" + val.toString() + ":" + workerContext.getTaskId());
    }
}
