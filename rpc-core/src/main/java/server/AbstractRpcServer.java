package server;

import annotation.Service;
import annotation.ServiceScan;
import entiry.RpcServer;
import enumeration.RpcError;
import exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ReflectUtil;

import java.util.Set;

public abstract class AbstractRpcServer implements RpcServer {
    private static final Logger logger = LoggerFactory.getLogger(AbstractRpcServer.class);
    public void scanServices() {
        Class<?> mainClass;
        String mainClassName = ReflectUtil.getMainClassName();
        try {
            mainClass = Thread.currentThread().getContextClassLoader().loadClass(mainClassName);
            if(!mainClass.isAnnotationPresent(ServiceScan.class)){
                logger.error("启动类缺少@ServiceScan注解");
                throw new RpcException(RpcError.SERVICE_SCAN_NOT_FOUND);
            }
        }
        catch (ClassNotFoundException e){
            logger.error("主类未发现(?)", e);
            throw new RpcException(RpcError.UNKNOW_ERROR);
        }
        String basePack = mainClass.getAnnotation(ServiceScan.class).value();
        if("".equals(basePack)){
            String[] names = mainClassName.split("\\.");
            if(names.length >1){
                basePack = names[0];
            }
        }
        Set<Class<?>> classes = ReflectUtil.findAllClass(basePack);
        for(Class<?> clazz :classes){
            if(clazz.isAnnotationPresent(Service.class)){
                String serviceName = clazz.getAnnotation(Service.class).name();
                Object service=null;
                if("".equals(serviceName)){
                    try {
                        service = clazz.newInstance();
                    } catch (InstantiationException |IllegalAccessException e) {
                        logger.error("对象创建失败");
                        throw new RpcException(RpcError.UNKNOW_ERROR);
                    }
                    for(Class<?> interface_ :clazz.getInterfaces()){
                        registry(service, interface_.getName());
                    }
                }
                else {
                    registry(service, serviceName);
                }
            }
        }
    }
}
