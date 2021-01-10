package exception;


import enumeration.RpcError;

public class RpcException extends RuntimeException{
    public RpcException(RpcError rpcError ,String detail)
    {
        super(rpcError.getMessage()+" : "+detail);
    }
    public RpcException(RpcError rpcError)
    {
        super(rpcError.getMessage());
    }
    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }
}
