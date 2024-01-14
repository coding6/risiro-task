package org.example;

import com.risirotask.interfaces.TaskConfig;
import com.risirotask.service.submitter.LocalTaskSubmitter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(SumTaskHandler.class);
        LocalTaskSubmitter localTaskSubmitter = new LocalTaskSubmitter(applicationContext);
        SumTask sumTask = new SumTask(5, 6);
        SumTaskConfig sumTaskConfig = new SumTaskConfig();
        for (int i = 0; i < 10; i++) {
            localTaskSubmitter.newTask(sumTask, sumTaskConfig).context("key1", "val1").asyncSubmit().subscribe();
        }

        ApplicationContext applicationContextB = new AnnotationConfigApplicationContext(Sum2TaskHandler.class);
        LocalTaskSubmitter localTaskSubmitter2 = new LocalTaskSubmitter(applicationContextB);
        SumTask2 sumTask2 = new SumTask2(2, 3);
        SumTaskConfig2 sumTaskConfig2 = new SumTaskConfig2();

        for (int i = 0; i < 10; i++) {
            localTaskSubmitter2.newTask(sumTask2, sumTaskConfig2).context("key1", "val2").asyncSubmit().subscribe();
        }
        Thread.sleep(100000);
    }

    static class SumTaskConfig implements TaskConfig {

        @Override
        public String getTaskConsumerBeanName() {
            return "sumTask";
        }

        @Override
        public long getRunningTimeout() {
            return 1000L;
        }
    }

    static class SumTaskConfig2 implements TaskConfig {

        @Override
        public String getTaskConsumerBeanName() {
            return "sumTask2";
        }

        @Override
        public long getRunningTimeout() {
            return 1000L;
        }
    }
}