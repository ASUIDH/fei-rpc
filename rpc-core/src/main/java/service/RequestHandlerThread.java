package service;

import entiry.RpcRequest;
import entiry.RpcResponse;
import exception.RpcException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import registry.ServiceRegistry;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@AllArgsConstructor
public class RequestHandlerThread implements  Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerThread.class);
    private RequestHandler requestHandler;
    private ServiceRegistry serviceRegistry;
    private Socket socket;
    @Override
    public void run() {
        try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream()))
        {
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            String interfaceName = rpcRequest.getInterfaceName();
            Object service = serviceRegistry.getService(interfaceName);
            Object result = requestHandler.handle(rpcRequest, service);
            objectOutputStream.writeObject(RpcResponse.success(result)); //这样写耦合度太高，很难根据result，决定success还是fail，应该之前就决定成功还是失败
            objectOutputStream.flush();
        }
        catch (IOException | ClassNotFoundException | RpcException e)
        {
            logger.error("调用发生错误",e);
        }
    }
}
