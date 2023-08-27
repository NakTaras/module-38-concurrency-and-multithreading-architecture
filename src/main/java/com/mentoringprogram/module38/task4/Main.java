package com.mentoringprogram.module38.task4;

import com.mentoringprogram.module38.task4.cache.Cache;
import com.mentoringprogram.module38.task4.cache.impl.LFUCache;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final Cache<String> stringCache = new LFUCache<>(4, 3);

    public static void main(String[] args) {
        Thread providerTread = new Thread(new Main.FirstThread());
        providerTread.start();

        Thread consumerThread = new Thread(new Main.SecondThread());
        consumerThread.start();

    }

    private static class FirstThread implements Runnable {

        @Override
        public void run() {
            List<String> strings = List.of("one", "two", "three", "four");
            try {
                for (String s : strings) {
                    stringCache.put(s);
                }
                for (String s : strings) {
                    stringCache.get(s);
                }
                stringCache.get("two");
                stringCache.get("two");

                TimeUnit.SECONDS.sleep(2);

                stringCache.get("one");
                stringCache.get("three");

                TimeUnit.SECONDS.sleep(2);

                stringCache.get("one");
                stringCache.get("four");
                stringCache.put("five");
                System.out.println(stringCache.getStatistic());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private static class SecondThread implements Runnable {

        @Override
        public void run() {
            List<String> strings = List.of("five", "two", "six", "four");
            try {
                for (String s : strings) {
                    stringCache.put(s);
                }
                for (String s : strings) {
                    stringCache.get(s);
                }
                stringCache.get("six");
                stringCache.get("two");

                TimeUnit.SECONDS.sleep(2);

                stringCache.get("four");
                stringCache.get("five");

                TimeUnit.SECONDS.sleep(2);

                stringCache.get("five");
                stringCache.get("five");
                stringCache.put("seven");
                System.out.println(stringCache.getStatistic());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
