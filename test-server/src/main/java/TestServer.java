import serializer.CommonSerializer;
import server.RpcSocketServer;

public class TestServer {


    public static void main(String[] args) {
        HelloService service =  new HelloServiceImpl();
        RpcSocketServer rpcServer = new RpcSocketServer("127.0.0.1:8848",9001);
        rpcServer.setSerializer(CommonSerializer.getByCode(2));
        rpcServer.start();

    }
}
