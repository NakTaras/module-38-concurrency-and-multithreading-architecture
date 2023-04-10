package com.mentoringprogram.module38.task1;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TaskWithHashMap {

  private static final Map<Integer, Integer> hashMap = new HashMap<>();

  public static void main(String[] args) throws InterruptedException {
    Thread addingTread = new Thread(new AddingThread());
    addingTread.start();

    Thread.sleep(5000);

    Thread summingThread = new Thread(new SummingThread());
    summingThread.start();
  }

  private static class AddingThread implements Runnable {

    @Override
    public void run() {
      int i = 0;
      while (true) {
        try {
          hashMap.put(i, i);
          System.out.println(Thread.currentThread().getName() + " added number " + i);
          i++;
          Thread.sleep(500);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }

  private static class SummingThread implements Runnable {

    @Override
    public void run() {
      int sum = 0;
      try {
        for (Integer num : hashMap.keySet()) {
            sum += num;
            System.out.println(Thread.currentThread().getName() + " current sum is " + sum);
            Thread.sleep(500);
        }
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      } catch (ConcurrentModificationException e) {
        Set<Thread> threads = Thread.getAllStackTraces().keySet();
        threads.forEach(Thread::interrupt);
        throw e;
      }
      System.out.println(Thread.currentThread().getName() + " final sum is " + sum);
    }
  }
}
