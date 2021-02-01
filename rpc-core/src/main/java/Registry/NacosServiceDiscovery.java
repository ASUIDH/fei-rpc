package Registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import enumeration.RpcError;
import exception.RpcException;
import loadbalancer.LoadBalancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

public class NacosServiceDiscovery implements  ServiceDiscovery {
    private static final Logger logger = LoggerFactory.getLogger(NacosServiceDiscovery.class);
    private NamingService naming;
    private LoadBalancer<Instance> loadBalancer;
    public NacosServiceDiscovery(String serverAddr,int balancerCode){
        try {
            naming = NamingFactory.createNamingService(serverAddr);
            this.loadBalancer = LoadBalancer.getByCode(balancerCode);
        } catch (NacosException e) {
            logger.error("Nacos服务器无法连接", e);
            throw new RpcException(RpcError.NACOS_CONNCT_FAILURE);
        }
    }
    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            List<Instance> instances = naming.getAllInstances(serviceName);
            Instance instance = loadBalancer.select(instances);
            InetSocketAddress inetSocketAddress = new InetSocketAddress(instance.getIp(), instance.getPort());
            logger.info("找到服务 : {} 地址为 : {} 的实例",serviceName,inetSocketAddress);
            return inetSocketAddress;
        }
        catch (NacosException e) {
            logger.error("服务发现失败", e);
            throw new RpcException(RpcError.NACOS_SERVICE_FIND_FAILURE);
        }
    }
}
