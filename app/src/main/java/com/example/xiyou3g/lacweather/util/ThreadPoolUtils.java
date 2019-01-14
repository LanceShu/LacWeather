package com.example.xiyou3g.lacweather.util;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Lance
 * on 2019/1/13.
 */

public class ThreadPoolUtils {
     private static ThreadPoolExecutor executor;
     private static ThreadPoolUtils threadPoolUtils;

     private ThreadPoolUtils() {}
     public static ThreadPoolUtils getInstance() {
         if (threadPoolUtils == null) {
             synchronized (ThreadPoolUtils.class) {
                 if (threadPoolUtils == null) {
                     threadPoolUtils = new ThreadPoolUtils();
                     executor = new ThreadPoolExecutor(4,
                             8, 60, TimeUnit.SECONDS,
                             new LinkedBlockingQueue<Runnable>(),
                             Executors.defaultThreadFactory());
                 }
             }
         }
         return threadPoolUtils;
     }

     public void excute(Runnable runnable) {
         if (threadPoolUtils == null)
             throw new IllegalStateException("the threadPoolUtils is not default");
         executor.execute(runnable);
     }
}
