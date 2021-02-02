package server;

import entiry.RpcRequest;
import entiry.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.lang.reflect.InvocationTargetException;

public class WorkThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(WorkThread.class);
    private Socket socket;
    private Object service;
    public WorkThread(Socket socket , Object service)
    {
        this.socket = socket;
        this.service = service;
    }
    @Override
    public void run() {
        try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());){
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            Method method =service.getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getParamTypes());//methodName
            Object returnObject = method.invoke(service,rpcRequest.getParameters());
            objectOutputStream.writeObject(RpcResponse.success(returnObject,rpcRequest.getRequestId()));
            objectOutputStream.flush();
        }
        catch (IOException | ClassNotFoundException |NoSuchMethodException  |IllegalAccessException | InvocationTargetException e ) {
            logger.error("调用或发送时发生错误",e);
        }
    }
}
