package com.alon.vuze.vuzemanager;

class DeletionStats {
  private static final int MB = 1024 * 1024 * 1024;
  private final Stats[] statsByDay;

  DeletionStats(int maxDays) {
    statsByDay = new Stats[maxDays];
    for (int i = 0; i < statsByDay.length; i++) {
      statsByDay[i] = new Stats();
    }
  }

  void add(int days, long size) {
    if (days < statsByDay.length) {
      statsByDay[days].add(size);
    }
  }

  int getNum(int day) {
    return day < statsByDay.length ? statsByDay[day].num : -1;
  }

  long getNumGb(int day) {
    return day < statsByDay.length ? (statsByDay[day].numBytes + MB / 2) / MB: -1;
  }

  private class Stats {
    private int num = 0;
    private long numBytes = 0;

    void add(long size) {
      num++;
      numBytes += size;
    }
  }

}
