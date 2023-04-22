package com.nyj.thread;

import java.util.concurrent.*;

/**
 * @author nyj
 * @version 1.0
 * @date 2023/4/19 9:14
 * 创建线程池，并通过线程池创建线程
 * *ThreadPoolExecutor()
 * *corePoolSize：线程池核心线程数量，表示当线程池中的线程数量小于该值时，会创建新的线程来执行任务。
 * *maximumPoolSize：线程池最大线程数量，表示线程池中允许存在的最大线程数量，当队列已满且线程数量达到该值时，新的任务将被拒绝执行。
 * *keepAliveTime：线程空闲时间，表示当线程池中的线程数量大于corePoolSize，并且空闲时间超过该值时，多余的线程会被销毁。
 * *unit：keepAliveTime的时间单位，如TimeUnit.SECONDS表示秒。
 * *workQueue：任务队列，表示存放等待执行的任务的队列。常用的有ArrayBlockingQueue、LinkedBlockingQueue和SynchronousQueue等。
 * *threadFactory：线程工厂，用于创建新的线程对象。可用于自定义线程名称、优先级等属性。
 * *handler：拒绝策略，表示当队列已满并且线程数量达到maximumPoolSize时，新提交的任务如何处理。常用的有AbortPolicy、CallerRunsPolicy、DiscardOldestPolicy和DiscardPolicy等。
 */
public class ThreadPool {
    ThreadPoolExecutor threadPoolExecutor;
    public ThreadPoolExecutor createThreadPoolExector(){
        threadPoolExecutor = new ThreadPoolExecutor(8, 8, 60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(6), Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
        return threadPoolExecutor;
    }
    public void execute(Runnable task){   //4个基站线程
        //System.out.println("jin");
        threadPoolExecutor.execute(task);
    }
    public <T> Future<T> submit(Callable<T> task){   //前端线程
        return threadPoolExecutor.submit(task);
    }
}
