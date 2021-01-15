import registry.DefaultServiceRegistry;
import service.NettyServer;

public class NettyTestServer {
    public static void main(String[] args) {
        NettyServer nettyServer = new NettyServer();
        HelloService helloServiceImpl = new HelloServiceImpl();
        DefaultServiceRegistry registry = new DefaultServiceRegistry();
        registry.registry(helloServiceImpl);
        nettyServer.start(9000);
    }
}
