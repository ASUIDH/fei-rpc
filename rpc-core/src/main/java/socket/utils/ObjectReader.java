package socket.utils;

import entiry.RpcRequest;
import entiry.RpcResponse;
import enumeration.PackageType;
import enumeration.RpcError;
import exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import serializer.CommonSerializer;

import java.io.IOException;
import java.io.InputStream;

public class ObjectReader {
    private static final Logger logger = LoggerFactory.getLogger(ObjectReader.class);
    private static final int MAGIC_NUMBER = 0x60937991;
    public static Object readObject (InputStream inputStream) throws IOException {
        byte[] intBytes = new byte[4];
        inputStream.read(intBytes);
        int magicNumber = getNumber(intBytes);
        if(magicNumber!=MAGIC_NUMBER)
        {
            logger.error("协议类型错误");
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }
        inputStream.read(intBytes);
        int typeCode = getNumber(intBytes);
        Class<?> clazz;
        if(typeCode == PackageType.REQUEST_PACK.getCode())
            clazz = RpcRequest.class;
        else if(typeCode == PackageType.RESPONSE_PACK.getCode())
            clazz = RpcResponse.class;
        else
        {
            logger.error("数据包类型错误");
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }
        inputStream.read(intBytes);
        int serializerCode = getNumber(intBytes);
        CommonSerializer serializer = CommonSerializer.getByCode(serializerCode);
        if(serializer == null)
        {
            logger.error("未知序列化方式");
            throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }
        inputStream.read(intBytes);
        int length = getNumber(intBytes);
        byte[] dataBytes = new byte [length];
        inputStream.read(dataBytes);
        Object obejct = serializer.deserialize(dataBytes, clazz);
        return obejct;
    }
    public static int getNumber(byte [] bytes)
    {
        int value; //这里有大小端的问题吗？
        value = bytes[0] & 0xff;
        value = value<<8 | bytes[1] & 0xff;
        value = value<<8 | bytes[2] & 0xff;
        value = value<<8 | bytes[3] & 0xff;
        return value;
    }
}
