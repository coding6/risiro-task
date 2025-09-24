package org.example;

import com.risirotask.core.executor.DynamicThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author coding6
 * @create 2025/9/20
 * @description
 */
@SpringBootApplication
@RestController
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Autowired
    private DynamicThreadPoolExecutor orderThreadPool;

    @GetMapping("/test")
    public String test() {
        System.out.println("Current thread pool status - Core pool size: " + orderThreadPool.getCorePoolSize() + 
                          ", Max pool size: " + orderThreadPool.getMaximumPoolSize() + 
                          ", Queue capacity: " + orderThreadPool.getWorkQueue().getCapacity() +
                          ", Current queue size: " + orderThreadPool.getQueue().size() +
                          ", Active threads: " + orderThreadPool.getActiveCount());
        
        int taskCount = 10; // 提交10个任务
        int acceptedTasks = 0;
        
        for (int i = 0; i < taskCount; i++) {
            final int taskId = i;
            try {
                orderThreadPool.execute(() -> {
                    try {
                        System.out.println("Task " + taskId + " started, sleeping for 5 seconds");
                        Thread.sleep(5000);
                        System.out.println("Task " + taskId + " completed");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                acceptedTasks++;
                System.out.println("Task " + taskId + " accepted, current queue size: " + orderThreadPool.getQueue().size());
            } catch (Exception e) {
                System.out.println("Task " + taskId + " rejected: " + e.getMessage());
            }
        }
        
        return "Submitted " + taskCount + " tasks, " + acceptedTasks + " were accepted";
    }

    @PutMapping("/update")
    public String updateConfig() {
        System.out.println("Before update - Core pool size: " + orderThreadPool.getCorePoolSize() + 
                          ", Max pool size: " + orderThreadPool.getMaximumPoolSize() + 
                          ", Queue capacity: " + orderThreadPool.getWorkQueue().getCapacity() +
                          ", Current queue size: " + orderThreadPool.getQueue().size());
        
        orderThreadPool.updateConfig(1, 1, 1);
        
        System.out.println("After update - Core pool size: " + orderThreadPool.getCorePoolSize() + 
                          ", Max pool size: " + orderThreadPool.getMaximumPoolSize() + 
                          ", Queue capacity: " + orderThreadPool.getWorkQueue().getCapacity() +
                          ", Current queue size: " + orderThreadPool.getQueue().size());
        
        return "Thread pool configuration updated successfully!";
    }
}
