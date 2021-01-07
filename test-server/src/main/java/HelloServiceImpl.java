import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloServiceImpl implements HelloService{
    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);
    @Override
    public String hello(HelloObject helloObject) {
        logger.info("接收到 id = {} 的消息",helloObject.getId());
        return "这是调用返回值,message = " + helloObject.getMessage();
    }
}
