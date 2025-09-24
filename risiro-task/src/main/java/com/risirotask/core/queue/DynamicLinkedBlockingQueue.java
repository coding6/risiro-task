package com.risirotask.core.queue;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author coding6
 * @create 2025/9/20
 * @description
 */
@Getter
@Slf4j
public class DynamicLinkedBlockingQueue<E> extends LinkedBlockingQueue<E> {

    private volatile int capacity;

    private final boolean forceChangeSize = false;

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    public DynamicLinkedBlockingQueue(int capacity) {
        super(capacity);
        this.capacity = capacity;
    }

    @Override
    public boolean offer(E e) {
        if (e == null) throw new NullPointerException();
        if (size() >= capacity) {
            return false;
        }
        return super.offer(e);
    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        if (e == null) throw new NullPointerException();
        if (size() >= capacity) {
            return false;
        }
        return super.offer(e, timeout, unit);
    }
    
    @Override
    public void put(E e) throws InterruptedException {
        if (e == null) throw new NullPointerException();
        
        lock.lockInterruptibly();
        try {
            // 如果当前大小已经达到或超过设置的容量，则等待
            while (size() >= capacity) {
                notFull.await(); // 等待队列不满的信号
            }
            
            // 添加元素
            super.offer(e);
        } finally {
            lock.unlock();
        }
    }
    
    @Override
    public int remainingCapacity() {
        return Math.max(0, capacity - size());
    }

    public void setCapacity(int newCapacity) {
        lock.lock();
        try {
            if (newCapacity < 0) {
                throw new IllegalArgumentException("Queue capacity can't be negative");
            }
            int oldCapacity = this.capacity;
            if (size() > newCapacity) {
                if (forceChangeSize) {
                    int elementsToRemove = size() - newCapacity;
                    for (int i = 0; i < elementsToRemove; i++) {
                        poll();
                    }
                } else {
                    throw new IllegalArgumentException("Queue size is greater than new capacity");
                }
            }
            this.capacity = newCapacity;
            System.out.println("Queue capacity changed from " + oldCapacity + " to " + newCapacity);
            
            // 如果容量增加了，唤醒等待的线程
            if (newCapacity > oldCapacity) {
                notFull.signalAll();
            }
        }  finally {
            lock.unlock();
        }
    }
}
