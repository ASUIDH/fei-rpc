package codec;

import entiry.RpcRequest;
import enumeration.PackageType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import serializer.CommonSerializer;

public class CommonEncoder extends MessageToByteEncoder {
    private static final int MAGIC_NUMBER = 0x60937991;
    private final CommonSerializer serializer;
    public CommonEncoder (CommonSerializer serializer)
    {
        this.serializer=serializer;
    }
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        byteBuf.writeInt(MAGIC_NUMBER);
        if(o instanceof RpcRequest)
        {
            byteBuf.writeInt(PackageType.REQUEST_PACK.getCode());
        }
        else
        {
            byteBuf.writeInt(PackageType.RESPONSE_PACK.getCode());
        }
        byte[] bytes = serializer.serialize(o);
        int serializerCode = serializer.getCode();
        int length = bytes.length;
        byteBuf.writeInt(serializerCode);
        byteBuf.writeInt(length);
        byteBuf.writeBytes(bytes);
    }
}