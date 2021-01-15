import registry.DefaultServiceRegistry;
import registry.ServiceRegistry;
import service.RpcSocketServer;

public class TestServer {


    public static void main(String[] args) {
        HelloService service =  new HelloServiceImpl();
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.registry(service);
        RpcSocketServer rpcServer = new RpcSocketServer(serviceRegistry);
        rpcServer.start(9000);

    }
}
