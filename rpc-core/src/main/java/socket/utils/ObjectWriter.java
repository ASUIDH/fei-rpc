package socket.utils;

import entiry.RpcRequest;
import enumeration.PackageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import serializer.CommonSerializer;

import java.io.IOException;
import java.io.OutputStream;

public class ObjectWriter {
    private static Logger logger = LoggerFactory.getLogger(ObjectWriter.class);
    private static final int MAGIC_NUMBER = 0x60937991;
    public static void writeObject(OutputStream outputStream, Object object,CommonSerializer serializer) throws IOException {
        byte [] magicBytes = setNumber(MAGIC_NUMBER);
        outputStream.write(magicBytes);
        if(object instanceof RpcRequest) {
            outputStream.write(setNumber(PackageType.REQUEST_PACK.getCode()));
        }
        else {
            outputStream.write(setNumber(PackageType.RESPONSE_PACK.getCode()));
        }
        int serializerCode =serializer.getCode();
        outputStream.write(setNumber(serializerCode));
        byte[] dataBytes = serializer.serialize(object);
        int length = dataBytes.length;
        outputStream.write(setNumber(length));
        outputStream.write(dataBytes);
        outputStream.flush();
    }
    public static byte[] setNumber(int value)
    {
        byte [] bytes = new byte[4];
        bytes[3] = (byte) (value & 0xff);
        bytes[2] = (byte) (value>>8 & 0xff);
        bytes[1] = (byte) (value>>16 & 0xff);
        bytes[0] = (byte) (value>>24 & 0xff);
        return bytes;
    }
}
