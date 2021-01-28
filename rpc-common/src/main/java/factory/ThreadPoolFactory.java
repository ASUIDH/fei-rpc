package factory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

public class ThreadPoolFactory {
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 20;
    private static final int KEEP_ALIVE_TIME = 30;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;
    private ThreadPoolFactory(){}
    public static ExecutorService createThreadPool(String threadNamePrefix)
    {
        return  createThreadPool(threadNamePrefix, false);
    }
    public static ExecutorService createThreadPool(String threadNamePrefix , boolean daemon)
    {
        BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<Runnable>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = getThreadFactory(threadNamePrefix,daemon);
        ExecutorService executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,blockingQueue,threadFactory);
        return executor;
    }
    private static ThreadFactory getThreadFactory(String threadNamePrefix , boolean daemon)
    {
        if(threadNamePrefix != null)
        {
            if(daemon == true)
            {
                return  new ThreadFactoryBuilder().setDaemon(true).setNameFormat(threadNamePrefix + "-%d").build();
            }
            return  new ThreadFactoryBuilder().setDaemon(false).setNameFormat(threadNamePrefix + "-%d").build();
        }
        return Executors.defaultThreadFactory();
    }
}
