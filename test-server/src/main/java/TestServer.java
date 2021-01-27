import Provider.DefaultServiceProvider;
import Provider.ServiceProvider;
import serializer.CommonSerializer;
import service.RpcSocketServer;

public class TestServer {


    public static void main(String[] args) {
        HelloService service =  new HelloServiceImpl();
        RpcSocketServer rpcServer = new RpcSocketServer("127.0.0.1:8848",9000);
        rpcServer.registry(service,HelloService.class);
        rpcServer.setSerializer(CommonSerializer.getByCode(2));
        rpcServer.start();

    }
}
