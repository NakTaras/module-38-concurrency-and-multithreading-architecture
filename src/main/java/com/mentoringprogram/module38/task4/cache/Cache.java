package com.mentoringprogram.module38.task4.cache;

public interface Cache<T> {
  void put(T cacheObject) throws InterruptedException;
  T get(T cacheObject) throws InterruptedException;
  String getStatistic();
}
