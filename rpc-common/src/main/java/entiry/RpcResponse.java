package entiry;

import enumeration.ResponseCode;
import lombok.Data;

import java.io.Serializable;

@Data
public class RpcResponse<T> implements Serializable {
    private Integer statesCode;
    private String message;
    private T data;
    public static <T> RpcResponse<T> success(T data)
    {
        RpcResponse <T> rpcResponse = new RpcResponse<>();
        rpcResponse.setData(data);
        rpcResponse.setStatesCode(ResponseCode.SUCCESS.getCode());
        return rpcResponse;
    }
    public static <T> RpcResponse <T> fail(ResponseCode code)
    {
        RpcResponse <T> rpcResponse = new RpcResponse<>();
        rpcResponse.setStatesCode(code.getCode());
        rpcResponse.setMessage(code.getMessage());
        return rpcResponse;
    }
}
