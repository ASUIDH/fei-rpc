import client.RpcSocketClient;
import entiry.RpcClient;
import entiry.RpcRequest;
import entiry.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

public class RpcClientProxy implements InvocationHandler {
    private final RpcClient client;
    RpcClientProxy(RpcClient client)
    {
        this.client = client;
    }
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz)
    {
        return (T)Proxy.newProxyInstance(clazz.getClassLoader(),new Class[]{clazz},this);
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest rpcRequest =  new RpcRequest(UUID.randomUUID().toString(),method.getDeclaringClass().getName(),method.getName(),args,method.getParameterTypes());
        return client.sendRequest(rpcRequest);
    }


}
