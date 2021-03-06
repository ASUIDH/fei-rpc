package Provider;

import enumeration.RpcError;
import exception.RpcException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultServiceProvider implements ServiceProvider {
    private static Map<String,Object> serviceMap = new ConcurrentHashMap<>();
    private static Set<String> registryService = ConcurrentHashMap.newKeySet();
    @Override
    public <T> void registry(T service) {
        String name = service.getClass().getName();
        synchronized (this)
        {
            if(!registryService.contains(name))
            {
                registryService.add(name);
            }
        }
        Class<?>[] interfaces = service.getClass().getInterfaces();
        for(Class<?> interface_ : interfaces) //这一步不用加锁
        {
            serviceMap.put(interface_.getName(),service);
        }
    }
    @Override
    public Object getService(String interfaceName) {
        Object service = serviceMap.get(interfaceName);
        if(service == null)
            throw new RpcException(RpcError.SERVICER_NOT_FOUNT);
        return service;
    }
}
