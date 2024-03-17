package com.risirotask.service.consumer.redis_consumer.interfaces;

public interface IWorker {
    /**
     * @description 单个worker的启动
     * @author coding6
     */
    void start();

    void stop();

}