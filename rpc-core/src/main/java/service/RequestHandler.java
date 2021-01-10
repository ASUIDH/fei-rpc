package service;

import entiry.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    public Object handle(RpcRequest rpcRequest , Object service)
    {
        Object result = null;
        try {
            result = invokerTargetMethod(rpcRequest, service);
            logger.info("服务 : {} 成功调用方法 : {}",rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (IllegalAccessException | InvocationTargetException |NoSuchMethodException e) {
            logger.error("调用或发送时有错误发生：", e); //其实我们应该也把这些错误信息直接发回客户端
        }
        return result;
    }
    public Object invokerTargetMethod(RpcRequest rpcRequest , Object service) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
        return  method.invoke(service, rpcRequest.getParameters());
    }
}
