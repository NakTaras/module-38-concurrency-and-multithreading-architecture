package com.mentoringprogram.module38.task3;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class AsynchronousQueue {
    private String[] messages;
    private int takeIndex;
    private int putIndex;
    private int count;
    private final ReentrantLock lock;
    private final Condition notEmpty;
    private final Condition notFull;

    public AsynchronousQueue(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException();
        }
        this.messages = new String[size];
        lock = new ReentrantLock();
        notEmpty = lock.newCondition();
        notFull =  lock.newCondition();
    }

    public void put(String message) throws InterruptedException {
        lock.lockInterruptibly();
        try {
            while (count == messages.length) {
                notFull.await();
            }
            System.out.println("Provider message: " + message);
            enqueue(message);
        } finally {
            lock.unlock();
        }
    }

    private void enqueue(String message) {
        messages[putIndex] = message;
        if (++putIndex == messages.length) {
            putIndex = 0;
        }
        count++;
        notEmpty.signal();
    }

    public String get() throws InterruptedException {
        lock.lockInterruptibly();
        try {
            while (count == 0)
                notEmpty.await();
            return dequeue();
        } finally {
            lock.unlock();
        }
    }

    private String dequeue() {
        final Object[] items = this.messages;
        String message = messages[takeIndex];
        items[takeIndex] = null;
        if (++takeIndex == items.length) {
            takeIndex = 0;
        }
        count--;
        notFull.signal();
        return message;
    }
}
