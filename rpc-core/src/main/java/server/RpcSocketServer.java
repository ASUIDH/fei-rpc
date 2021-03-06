package server;

import Provider.DefaultServiceProvider;
import Registry.DefaultServiceRegistry;
import Registry.ServiceRegistry;
import entiry.RpcServer;
import factory.SingletonFactory;
import hook.ShutdownHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Provider.ServiceProvider;
import serializer.CommonSerializer;
import factory.ThreadPoolFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class RpcSocketServer extends AbstractRpcServer {
    private static final Logger logger = LoggerFactory.getLogger(RpcSocketServer.class);
    private final ExecutorService threadpool;
    private final ServiceProvider serviceProvider;
    private RequestHandler requestHandler;
    private ServiceRegistry registry;
    private CommonSerializer serializer;
    private int port;
    public RpcSocketServer(String nacosServerAddr , int port)
    {
        this.serviceProvider = new DefaultServiceProvider();
        this.registry = new DefaultServiceRegistry(nacosServerAddr);
        this.port = port;
        this.threadpool = ThreadPoolFactory.createThreadPool("socket-rpc-server");
        requestHandler = SingletonFactory.getInstance(RequestHandler.class);
    }

    @Override
    public <T> void registry(Object service, String serviceName) {
        registry.registry(serviceName,"127.0.0.1",port);
        serviceProvider.registry(service); //这里会调用多次,后续考虑改进
    }

    public void start()
    {
        scanServices();
        try (ServerSocket serverSocket = new ServerSocket(port);){
            logger.info("服务启动");
            Socket socket;
            ShutdownHook.getShutdownHook().addCleanAllHook();
            while((socket = serverSocket.accept())!=null)
            {
                logger.info("客户端连接，ip为{}",socket.getInetAddress());
                threadpool.execute(new RequestHandlerThread(requestHandler, serviceProvider,socket,serializer));
            }
        }
        catch (IOException e)
        {
            logger.error("服务端启动出现问题", e);
        }
    }

    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
}
