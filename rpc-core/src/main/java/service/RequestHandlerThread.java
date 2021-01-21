package service;

import entiry.RpcRequest;
import entiry.RpcResponse;
import exception.RpcException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import registry.ServiceRegistry;
import serializer.CommonSerializer;
import socket.utils.ObjectReader;
import socket.utils.ObjectWriter;

import java.io.*;
import java.net.Socket;

@AllArgsConstructor
public class RequestHandlerThread implements  Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerThread.class);
    private RequestHandler requestHandler;
    private ServiceRegistry serviceRegistry;
    private Socket socket;
    private CommonSerializer serializer;
    @Override
    public void run() {
        try(OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();)
        {
            RpcRequest rpcRequest = (RpcRequest) ObjectReader.readObject(inputStream);
            String interfaceName = rpcRequest.getInterfaceName();
            Object service = serviceRegistry.getService(interfaceName);
            Object result = requestHandler.handle(rpcRequest, service);
            ObjectWriter.writeObject(outputStream, RpcResponse.success(result), serializer);
            outputStream.flush();
        }
        catch (IOException | RpcException e)
        {
            logger.error("调用发生错误",e);
        }
    }
}
