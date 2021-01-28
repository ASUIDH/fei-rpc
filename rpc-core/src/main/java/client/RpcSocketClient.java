package client;

import Registry.DefaultServiceRegistry;
import Registry.NacosServiceDiscovery;
import Registry.ServiceDiscovery;
import Registry.ServiceRegistry;
import entiry.RpcClient;
import entiry.RpcRequest;
import entiry.RpcResponse;
import enumeration.ResponseCode;
import enumeration.RpcError;
import exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import serializer.CommonSerializer;
import socket.utils.ObjectReader;
import socket.utils.ObjectWriter;
import util.RpcMessageChecker;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class RpcSocketClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(RpcSocketClient.class);
    private String host ;
    private int port;
    private ServiceDiscovery discovery;

    private CommonSerializer serializer;
    public RpcSocketClient(String host ,int port)
    {
        this.host =host;
        this.port=port;
        discovery= new NacosServiceDiscovery(host+":"+port);

    }
    public Object sendRequest(RpcRequest rpcRequest) {
        if(serializer == null) {
            logger.error("未初始化序列化方式");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        InetSocketAddress inetSocketAddress =discovery.lookupService(rpcRequest.getInterfaceName());
        try(Socket socket = new Socket(inetSocketAddress.getHostName(),inetSocketAddress.getPort());
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();)
        {
            ObjectWriter.writeObject(outputStream, rpcRequest, serializer);
            Object obj =  ObjectReader.readObject(inputStream);
            RpcResponse response = (RpcResponse) obj;
            RpcMessageChecker.check(rpcRequest, response);
            return response.getData();
        }
        catch (IOException e)
        {
            logger.error("调用时发生错误",e);
            return null;
        }
    }

    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
}
