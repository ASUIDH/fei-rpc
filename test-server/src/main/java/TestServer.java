import registry.DefaultServiceRegistry;
import registry.ServiceRegistry;
import service.RpcServer;

public class TestServer {


    public static void main(String[] args) {
        HelloService service =  new HelloServiceImpl();
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.registry(service);
        RpcServer rpcServer = new RpcServer(serviceRegistry);
        rpcServer.start(9000);

    }
}
