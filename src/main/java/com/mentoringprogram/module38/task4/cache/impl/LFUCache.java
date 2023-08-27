package com.mentoringprogram.module38.task4.cache.impl;

import com.mentoringprogram.module38.task4.cache.Cache;
import com.mentoringprogram.module38.task4.cache.Pair;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class LFUCache<T> implements Cache<T> {

  private final int cacheSize;
  private final int timeToLive;
  private final Map<T, Pair> cache = new HashMap<>();
  private final List<Long> timesForPutting = new LinkedList<>();
  private int cacheEvictionsNum = 0;
  private final ReentrantLock lock;

  public LFUCache(int cacheSize, int timeToLive) {
    if (cacheSize <= 0) {
      throw new IllegalArgumentException();
    }
    this.cacheSize = cacheSize;
    this.timeToLive = timeToLive;
    lock = new ReentrantLock();
  }

  @Override
  public void put(T cacheObject) throws InterruptedException {
    LocalTime startTime = LocalTime.now();
    lock.lockInterruptibly();
    removeAfterTimeToLive();

    if (cache.size() == cacheSize) {
      T lfuCachedValue = findLFU();
      System.out.println(Thread.currentThread().getName() + " -> Cache block " + lfuCachedValue
          + " removed.");
      cache.remove(lfuCachedValue);
      cacheEvictionsNum++;
    }

    Pair newPair = new Pair();
    cache.put(cacheObject, newPair);
    System.out.println(Thread.currentThread().getName() + " -> Cache block " + cacheObject
        + " inserted.");
    lock.unlock();
    LocalTime endTime = LocalTime.now();
    timesForPutting.add(ChronoUnit.MICROS.between(startTime, endTime));
  }

  @Override
  public T get(T cacheObject) throws InterruptedException {
    lock.lockInterruptibly();
    removeAfterTimeToLive();

    if (cache.containsKey(cacheObject)) {
      Pair pair = cache.get(cacheObject);
      pair.incrementFrequency();
      pair.updateTime();
      System.out.println(Thread.currentThread().getName() + " -> Cache block " + cacheObject
          + " used.");
    }
    lock.unlock();
    return null;
  }

  @Override
  public String getStatistic() {
    return Thread.currentThread().getName() + " -> Average time spent for putting new values into the cache: "
            + Double.valueOf(getAverageTimeForPutting()).longValue() + " microseconds\n"
            + "Number of cache evictions: " + getCacheEvictionsNum();
  }

  private T findLFU() {
    T lfuCachedObject = null;
    int minFrequency = Integer.MAX_VALUE;

    for (Map.Entry<T, Pair> entry : cache.entrySet()) {
      if (entry.getValue().getFrequency() < minFrequency) {
        minFrequency = entry.getValue().getFrequency();
        lfuCachedObject = entry.getKey();
      }
    }

    return lfuCachedObject;
  }

  private void removeAfterTimeToLive() {
    List<T> cachedObjectsToRemove = cache.entrySet().stream()
        .filter(cacheEntry -> ChronoUnit.SECONDS.between(cacheEntry.getValue().getUpdateTime(), LocalTime.now()) > timeToLive)
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());

    for (T cachedObject : cachedObjectsToRemove) {
      cache.remove(cachedObject);
      cacheEvictionsNum++;
      System.out.println(Thread.currentThread().getName() + " -> Cache block " + cachedObject
          + " removed.");
    }
  }

  public double getAverageTimeForPutting() {
    return timesForPutting.stream().mapToDouble(Long::doubleValue).average().orElse(0);
  }

  public int getCacheEvictionsNum() {
    return cacheEvictionsNum;
  }
}
