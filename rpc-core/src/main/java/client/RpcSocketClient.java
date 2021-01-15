package client;

import entiry.RpcClient;
import entiry.RpcRequest;
import entiry.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RpcSocketClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(RpcSocketClient.class);
    private String host ;
    private int port;
    public RpcSocketClient(String host ,int port)
    {
        this.host =host;
        this.port=port;
    }
    public Object sendRequest(RpcRequest rpcRequest) {
        try(Socket socket = new Socket(host, port);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());)
        {
            objectOutputStream.writeObject(rpcRequest);
            objectOutputStream.flush();
            return ((RpcResponse)objectInputStream.readObject()).getData();
        }
        catch (IOException | ClassNotFoundException e)
        {
            logger.error("调用时发生错误",e);
            return null;
        }
    }

}
