package enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RpcError {

    SERVICER_NOT_FOUNT("找不到该服务"),
    SERVICE_NOT_IMPLEMENT_ANY_INTERFACE("注册的服务未实现任何接口"),
    UNKNOWN_PROTOCOL("协议错误"),
    UNKNOWN_PACKAGE_TYPE("数据包类型错误"),
    UNKNOWN_SERIALIZER("未知序列化方式");
    private String message;
}
