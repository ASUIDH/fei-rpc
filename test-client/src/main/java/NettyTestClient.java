import client.NettyClient;
import entiry.RpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyTestClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyTestClient.class);
    public static void main(String[] args) {
        RpcClient client = new NettyClient("127.0.0.1", 9000);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService serviceProx = rpcClientProxy.getProxy(HelloService.class);
        String ans = serviceProx.hello(new HelloObject(1, "啦啦啦"));
        logger.info(ans);
    }
}
