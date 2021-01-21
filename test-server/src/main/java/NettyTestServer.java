import registry.DefaultServiceRegistry;
import serializer.CommonSerializer;
import service.NettyServer;

public class NettyTestServer {
    public static void main(String[] args) {
        NettyServer nettyServer = new NettyServer();
        nettyServer.setSerializer(CommonSerializer.getByCode(3));
        HelloService helloServiceImpl = new HelloServiceImpl();
        DefaultServiceRegistry registry = new DefaultServiceRegistry();
        registry.registry(helloServiceImpl);
        nettyServer.start(9000);
    }
}
