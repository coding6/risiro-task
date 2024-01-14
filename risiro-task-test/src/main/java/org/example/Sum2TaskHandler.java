package org.example;

import com.risirotask.handler.TaskRunnableHandler;
import com.risirotask.interfaces.data.WorkerContext;
import org.springframework.stereotype.Component;

@Component("sumTask2")
public class Sum2TaskHandler implements TaskRunnableHandler<SumTask2> {

    @Override
    public void handler(SumTask2 taskRequest, WorkerContext workerContext) {
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        Object val = workerContext.getContext().get("key1");
        System.out.println("sumTask2收到回调：" + taskRequest.getA() + ":" + taskRequest.getB() + ":" + val.toString() + ":" + workerContext.getTaskId());
    }
}
