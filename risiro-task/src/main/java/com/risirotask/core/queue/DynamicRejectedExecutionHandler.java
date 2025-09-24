package com.risirotask.core.queue;

import lombok.Data;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author coding6
 * @create 2025/9/20
 * @description
 */
@Data
public class DynamicRejectedExecutionHandler implements RejectedExecutionHandler {

    private volatile RejectedExecutionHandler currentHandler;
    public DynamicRejectedExecutionHandler(RejectedExecutionHandler currentHandler) {
        this.currentHandler = currentHandler;
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        currentHandler.rejectedExecution(r, executor);
    }
}