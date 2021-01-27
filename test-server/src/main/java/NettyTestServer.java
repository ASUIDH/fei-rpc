import Provider.DefaultServiceProvider;
import serializer.CommonSerializer;
import service.NettyServer;

public class NettyTestServer {
    public static void main(String[] args) {
        NettyServer nettyServer = new NettyServer("127.0.0.1:8848",9000);
        nettyServer.setSerializer(CommonSerializer.getByCode(0));
        HelloService helloServiceImpl = new HelloServiceImpl();
        nettyServer.registry(helloServiceImpl, HelloService.class);
        nettyServer.start();
    }
}
