import client.RpcClient;
import entiry.RpcRequest;
import entiry.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RpcClientProxy implements InvocationHandler {
    String host;
    int port;
    RpcClientProxy(String host,int port)
    {
        this.host = host;
        this.port = port;
    }
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz)
    {
        return (T)Proxy.newProxyInstance(clazz.getClassLoader(),new Class[]{clazz},this);
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest rpcRequest =  RpcRequest.builder()
                                .interfaceName(method.getDeclaringClass().getName()) // .interfaceName(method.getDeclaringClass().getName()) 为啥这么写啊？
                                .methodName(method.getName())
                                .parameters(args)
                                .paramTypes(method.getParameterTypes())
                                .build();
        RpcClient rpcClient = new RpcClient();
        return ((RpcResponse)rpcClient.sendRequest(rpcRequest,host,port)).getData();
    }


}
