package util;

import entiry.RpcRequest;
import entiry.RpcResponse;
import enumeration.ResponseCode;
import enumeration.RpcError;
import exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcMessageChecker {
    private static final Logger logger = LoggerFactory.getLogger(RpcMessageChecker.class);
    public static void check(RpcRequest rpcRequest, RpcResponse rpcResponse)
    {
        if(rpcResponse == null)
        {
            logger.error("调用服务失败,serviceName:{}", rpcRequest.getInterfaceName());
            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE,"interfaceName : "+rpcRequest.getInterfaceName() + " id : "+ rpcRequest.getRequestId());
        }
        if(!rpcResponse.getRequestId().equals(rpcRequest.getRequestId()))
        {
            logger.error("请求和响应不匹配");
            throw new RpcException(RpcError.RESPONSE_NOT_MATCH,"interfaceName : "+rpcRequest.getInterfaceName());
        }
        if(rpcResponse.getStatesCode()==null || rpcResponse.getStatesCode()!= ResponseCode.SUCCESS.getCode())
        {
            logger.error("调用服务失败,serviceName:{},RpcResponse:{}", rpcRequest.getInterfaceName(), rpcResponse);
            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE,"interfaceName : "+rpcRequest.getInterfaceName() +" id : "+ rpcRequest.getRequestId());
        }
    }
}
