package factory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Function;

public class ThreadPoolFactory {
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 20;
    private static final int KEEP_ALIVE_TIME = 30;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;
    private static final Logger logger = LoggerFactory.getLogger(ThreadFactory.class);
    private static Map<String,ExecutorService> threadPoolMap =  new ConcurrentHashMap<>();
    private ThreadPoolFactory(){}
    public static ExecutorService createThreadPool(String threadNamePrefix)
    {
        ExecutorService threadPool = threadPoolMap.computeIfAbsent(threadNamePrefix, new Function<String, ExecutorService>() {
            @Override
            public ExecutorService apply(String s) {
                return createThreadPool(threadNamePrefix,false);
            }
        });
            if (threadPool == null || threadPool.isShutdown() || threadPool.isTerminated()) {
                synchronized (threadPool)
                {
                    threadPool = threadPoolMap.get(threadNamePrefix);
                    if(threadPool == null || threadPool.isShutdown() || threadPool.isTerminated())
                    {
                        threadPool = createThreadPool(threadNamePrefix, false);
                        threadPoolMap.put(threadNamePrefix, threadPool);
                    }
                }
            }
            return threadPool;
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
    public static void shutdownAll()
    {
        threadPoolMap.entrySet().parallelStream().forEach(entry -> {
            ExecutorService threadPool = entry.getValue();
            threadPool.shutdown();
            logger.info("关闭线程池 : {} , {} ",entry.getKey(),threadPool.isTerminated());
            try{
                threadPool.awaitTermination(10, TimeUnit.SECONDS);
            }
            catch (InterruptedException e)
            {
                logger.error("线程池 : {} 关闭失败", entry.getKey());
                threadPool.shutdownNow();
            }
    });
    }
}
