package com.mentoringprogram.module38.task2;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Set;


public class DeadlocksWithSynchronization {

  private static final Object summingLock = new Object();
  private static final Object squareRootLock = new Object();

  private static final List<Integer> numbers = new ArrayList<>();

  public static void main(String[] args) throws InterruptedException {
    Thread addingTread = new Thread(new AddingThread());
    addingTread.start();

    Thread.sleep(5000);

    Thread summingThread = new Thread(new SummingThread());
    summingThread.start();

    Thread.sleep(1000);

    Thread squareRootThread = new Thread(new SquareRootThread());
    squareRootThread.start();
  }

  private static class AddingThread implements Runnable {

    @Override
    public void run() {
      int i = 0;
      while (true) {
        try {
          synchronized (summingLock) {
            synchronized (squareRootLock) {
              numbers.add(i);
            }
          }
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
        synchronized (summingLock) {
          for (Integer num : numbers) {
            sum += num;
            System.out.println(Thread.currentThread().getName() + " current sum is " + sum);
            Thread.sleep(500);
          }
        }
      } catch (ConcurrentModificationException e) {
        Set<Thread> threads = Thread.getAllStackTraces().keySet();
        threads.forEach(Thread::interrupt);
        throw e;
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      System.out.println(Thread.currentThread().getName() + " final sum is " + sum);
    }
  }

  private static class SquareRootThread implements Runnable {

    @Override
    public void run() {
      int squaresSum = 0;
      try {
        synchronized (squareRootLock) {
          for (Integer num : numbers) {
            squaresSum += num * num;
            System.out.println(Thread.currentThread().getName() + " current sum of squares is " + squaresSum);
            Thread.sleep(500);
          }
        }
      } catch (ConcurrentModificationException e) {
        Set<Thread> threads = Thread.getAllStackTraces().keySet();
        threads.forEach(Thread::interrupt);
        throw e;
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      System.out.println(Thread.currentThread().getName() + " final sum of squares is " + squaresSum);
      System.out.println(Thread.currentThread().getName() + " square root is " +  Math.sqrt(squaresSum));
    }
  }
}
