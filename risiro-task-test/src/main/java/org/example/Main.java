package org.example;

import com.risirotask.annotation.AutoRisiroTask;
import com.risirotask.config.WorkerAutoConfiguration;
import com.risirotask.service.submitter.LocalTaskSubmitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AutoRisiroTask
@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @GetMapping("/test")
    public void test() throws InterruptedException {
//        RedisTaskSubmitter redisTaskSubmitter = new RedisTaskSubmitter();
//        SumTask sumTask = new SumTask(5, 6);
//        for (int i = 0; i < 10; i++) {
//            redisTaskSubmitter
//                    .newTask(sumTask, "org.example.SumTaskHandler#0")
//                    .context("key1", "val1")
//                    .asyncSubmit()
//                    .subscribe();
//        }

        LocalTaskSubmitter localTaskSubmitter2 = new LocalTaskSubmitter();
        SumTask2 sumTask2 = new SumTask2(2, 3);

        for (int i = 0; i < 200; i++) {
            localTaskSubmitter2.newTask(sumTask2, "org.example.Sum2TaskHandler#0").context("key1", "val2").submit().subscribe();
        }

//        Thread.sleep(2000);
//        List<IWorker> values = WorkerManager.ALL_WORKERS.get("org.example.Sum2TaskHandler#0");
//        WorkerManager.getInstance().stop(new ArrayList<>(values));
        Thread.sleep(100000);

    }

}