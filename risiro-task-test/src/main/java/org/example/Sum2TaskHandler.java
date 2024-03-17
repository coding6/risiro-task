package org.example;

import com.risirotask.handler.TaskRunnableHandler;
import com.risirotask.service.data.WorkerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component("sumTask2")
@Slf4j
public class Sum2TaskHandler implements TaskRunnableHandler<SumTask2> {

    @Override
    public void handler(SumTask2 taskRequest, WorkerContext workerContext) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            return;
        }
        Object val = workerContext.getContext().get("key1");
        System.out.println("sumTask2收到回调：" + taskRequest.getA() + ":" + taskRequest.getB() + ":" + val.toString() + ":" + workerContext.getTaskId());
    }

    @Override
    public void fail(SumTask2 taskRequest, WorkerContext workerContext) {
        log.error("sumTask2任务失败了:{}", workerContext.getTaskId());
    }
}
