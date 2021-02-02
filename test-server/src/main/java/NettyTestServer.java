import annotation.ServiceScan;
import serializer.CommonSerializer;
import server.NettyServer;

@ServiceScan
public class NettyTestServer {
    public static void main(String[] args) {
        NettyServer nettyServer = new NettyServer("127.0.0.1:8848",9000);
        nettyServer.setSerializer(CommonSerializer.getByCode(0));
        nettyServer.start();
    }
}
