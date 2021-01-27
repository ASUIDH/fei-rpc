package service;

import entiry.RpcServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import registry.ServiceRegistry;
import serializer.CommonSerializer;
import util.ThreadPoolFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class RpcSocketServer implements RpcServer {
    private static final Logger logger = LoggerFactory.getLogger(RpcSocketServer.class);
    private final ExecutorService threadpool;
    private final ServiceRegistry serviceRegistry;
    private RequestHandler requestHandler;
    private CommonSerializer serializer;
    public RpcSocketServer(ServiceRegistry serviceRegistry)
    {
        this.serviceRegistry = serviceRegistry;
        this.threadpool = ThreadPoolFactory.createThreadPool("socket-rpc-server");
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
                threadpool.execute(new RequestHandlerThread(requestHandler,serviceRegistry,socket,serializer));
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
