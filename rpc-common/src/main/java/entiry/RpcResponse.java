package entiry;

import enumeration.ResponseCode;
import lombok.Data;

import java.io.Serializable;

@Data
public class RpcResponse<T> implements Serializable {
    private String requestId;
    private Integer statesCode;
    private String message;
    private T data;
    public RpcResponse(){}
    public static <T> RpcResponse<T> success(T data,String requestId)
    {
        RpcResponse <T> rpcResponse = new RpcResponse<>();
        rpcResponse.setData(data);
        rpcResponse.setRequestId(requestId);
        rpcResponse.setStatesCode(ResponseCode.SUCCESS.getCode());
        return rpcResponse;
    }
    public static <T> RpcResponse <T> fail(ResponseCode code,String requestId)
    {
        RpcResponse <T> rpcResponse = new RpcResponse<>();
        rpcResponse.setRequestId(requestId);
        rpcResponse.setStatesCode(code.getCode());
        rpcResponse.setMessage(code.getMessage());
        return rpcResponse;
    }
}
