package service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import registry.ServiceRegistry;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class RpcServer {
    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);
    private final ExecutorService threadpool;
    private final ServiceRegistry serviceRegistry;
    private RequestHandler requestHandler;
    public RpcServer(ServiceRegistry serviceRegistry)
    {
        this.serviceRegistry = serviceRegistry;
        int corePoolSize = 3;
        int maximumPoolSize = 30;
        int keepAlive = 60;
        BlockingQueue<Runnable> bq = new ArrayBlockingQueue<>(100);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        this.threadpool = new ThreadPoolExecutor(corePoolSize,maximumPoolSize,keepAlive,TimeUnit.SECONDS,bq,threadFactory);
        requestHandler = new RequestHandler();
    }
    public void start(int port)
    {
        try (ServerSocket serverSocket = new ServerSocket(port);){
            logger.info("服务启动");
            Socket socket;
            while((socket = serverSocket.accept())!=null)
            {
                logger.info("客户端连接，ip为{}",socket.getInetAddress());
                threadpool.execute(new RequestHandlerThread(requestHandler,serviceRegistry,socket));
            }
        }
        catch (IOException e)
        {
            logger.error("服务端启动出现问题", e);
        }
    }
}
