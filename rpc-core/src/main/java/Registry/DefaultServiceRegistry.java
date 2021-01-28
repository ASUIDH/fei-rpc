package Registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import enumeration.RpcError;
import exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

public class DefaultServiceRegistry implements ServiceRegistry{
    private static final Logger logger = LoggerFactory.getLogger(DefaultServiceRegistry.class);
    private NamingService naming;
    public DefaultServiceRegistry(String serverAddr)
    {
        try {
            naming = NamingFactory.createNamingService(serverAddr);
        } catch (NacosException e) {
            logger.error("Nacos服务器无法连接", e);
            throw new RpcException(RpcError.NACOS_CONNCT_FAILURE);
        }
    }
    @Override
    public void registry(String serviceName, String host, int port) {
        try {
            naming.registerInstance(serviceName, host, port);
        }catch (NacosException e) {
            logger.error("注册服务失败", e);
            throw new RpcException(RpcError.NACOS_REGISTRY_FAILURE);
        }
    }
}
