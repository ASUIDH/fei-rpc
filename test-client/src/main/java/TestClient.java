import client.RpcSocketClient;
import entiry.RpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import serializer.CommonSerializer;

public class TestClient {
    private static final Logger logger = LoggerFactory.getLogger(TestClient.class);
    public static void main(String[] args) {
        RpcSocketClient rpcSocketClient = new RpcSocketClient("127.0.0.1", 8848);
        rpcSocketClient.setSerializer(CommonSerializer.getByCode(1));
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcSocketClient);
        HelloService helloServiceProx =rpcClientProxy.getProxy(HelloService.class);
        for(int i = 0 ; i< 9;i++) {
            String ans = helloServiceProx.hello(new HelloObject(1, "啦啦啦"));
            logger.info(ans);
        }
    }
}
