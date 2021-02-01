package Registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;

import enumeration.RpcError;
import exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultServiceRegistry implements ServiceRegistry{
    private static final Logger logger = LoggerFactory.getLogger(DefaultServiceRegistry.class);
    private NamingService naming;
    private Map<String, InetSocketAddress> serviceMap = new HashMap<>();
    private static List<ServiceRegistry> serviceRegistries = new CopyOnWriteArrayList<>();
    public DefaultServiceRegistry(String serverAddr)
    {
        try {
            naming = NamingFactory.createNamingService(serverAddr);
            serviceRegistries.add(this);
        } catch (NacosException e) {
            logger.error("Nacos服务器无法连接", e);
            throw new RpcException(RpcError.NACOS_CONNCT_FAILURE);
        }
    }
    @Override
    public void registry(String serviceName, String host, int port) {
        try {
            naming.registerInstance(serviceName, host, port);
            serviceMap.put(serviceName,new InetSocketAddress(host, port));
        }catch (NacosException e) {
            logger.error("注册服务失败", e);
            throw new RpcException(RpcError.NACOS_REGISTRY_FAILURE);
        }
    }
    public void cleanRegistry()//为啥只有debug触发?
    {
            for(Map.Entry<String,InetSocketAddress> entry : serviceMap.entrySet())
            {
                String serviceName = entry.getKey();
                InetSocketAddress addr = entry.getValue();
                try{
                    naming.deregisterInstance(serviceName, addr.getHostName(), addr.getPort());
                    logger.info("注销服务 : {}成功",serviceName);
                }
                catch (NacosException e) {
                    logger.error("注销服务 : {}失败",serviceName, e);
                }
            }
    }
    public static void cleanAllRegistry(){
        DefaultServiceRegistry.getServiceRegistries().parallelStream().forEach(serviceRegistry -> {
            serviceRegistry.cleanRegistry();
        });
    }

    public static List<ServiceRegistry> getServiceRegistries() {
        return serviceRegistries;
    }
}
