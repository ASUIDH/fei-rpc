package hook;

import Registry.DefaultServiceRegistry;
import Registry.ServiceRegistry;
import factory.ThreadPoolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShutdownHook {
    private static final Logger logger = LoggerFactory.getLogger(ShutdownHook.class);
    private static final ShutdownHook shutdownHook = new ShutdownHook();

    public static ShutdownHook getShutdownHook() {
        return shutdownHook;
    }
    public void addCleanAllHook(){
        logger.info("关闭后将自动注销所有服务");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DefaultServiceRegistry.cleanAllRegistry();
            ThreadPoolFactory.shutdownAll();
        }));
    }
}
