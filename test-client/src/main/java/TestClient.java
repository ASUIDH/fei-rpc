import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestClient {
    private static final Logger logger = LoggerFactory.getLogger(TestClient.class);
    public static void main(String[] args) {
        RpcClientProxy rpcClientProxy = new RpcClientProxy("127.0.0.1", 9000);
        HelloService helloServiceProx =rpcClientProxy.getProxy(HelloService.class);
        String ans = helloServiceProx.hello(new HelloObject(1, "啦啦啦"));
        logger.info(ans);
    }
}
