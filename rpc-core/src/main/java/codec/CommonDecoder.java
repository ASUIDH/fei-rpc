package codec;

import entiry.RpcRequest;
import entiry.RpcResponse;
import enumeration.PackageType;
import enumeration.RpcError;
import exception.RpcException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import serializer.CommonSerializer;

import java.util.List;

public class CommonDecoder extends ReplayingDecoder {
    private static final int MAGIC_NUMBER = 0x60937991;
    private CommonSerializer serializer;
    private final static Logger logger = LoggerFactory.getLogger(CommonDecoder.class);
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int magicNumber = byteBuf.readInt();
        if(magicNumber !=MAGIC_NUMBER)
        {
            logger.error("协议类型错误");
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }
        int typeCode = byteBuf.readInt();
        Class<?> clazz = null;
        if(typeCode == PackageType.REQUEST_PACK.getCode())
        {
            clazz = RpcRequest.class;
        }
        else if(typeCode == PackageType.RESPONSE_PACK.getCode())
        {
            clazz = RpcResponse.class;
        }
        else
        {
            logger.error("数据包类型错误");
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE); //这里throw出去，在下一个handlre处理得意思吗
        }
        int serializerCode = byteBuf.readInt();
        serializer = CommonSerializer.getByCode(serializerCode);
        if(serializer == null)
        {
            throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }
        int len = byteBuf.readInt();
        byte [] bytes = new byte[len];
        byteBuf.readBytes(bytes);//居然这样就读进来了
        Object obj = serializer.deserialize(bytes, clazz);
        list.add(obj);
    }
}
