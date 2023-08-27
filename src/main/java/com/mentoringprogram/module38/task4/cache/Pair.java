package com.mentoringprogram.module38.task4.cache;

import java.time.LocalTime;

public class Pair {
  private int frequency;
  private LocalTime updateTime;

  public Pair() {
    this.frequency = 1;
    this.updateTime = LocalTime.now();
  }

  public int getFrequency() {
    return frequency;
  }

  public void incrementFrequency() {
    this.frequency++;
  }

  public LocalTime getUpdateTime() {
    return updateTime;
  }

  public void updateTime() {
    this.updateTime = LocalTime.now();
  }
}
