package serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import entiry.RpcRequest;
import entiry.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class KryoSerializer implements CommonSerializer {
    private static final Logger logger = LoggerFactory.getLogger(KryoSerializer.class);
    ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(()->{
        Kryo kryo = new Kryo();
        kryo.setReferences(true);//对引用序列化
        kryo.setRegistrationRequired(false);//不用注册为好
        return kryo;
    });
    int code = 0;
    @Override
    public byte[] serialize(Object obj) {
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream())
        {
            Kryo kryo = kryoThreadLocal.get();
            Output output = new Output(byteArrayOutputStream);
            kryo.writeObject(output,obj);
            //output.close();
            kryoThreadLocal.remove();
            return output.toBytes();
        }
        catch (IOException e) {
            logger.error("序列化时发生错误",e);
            return null;
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try(ByteArrayInputStream byteArrayOutputStream = new ByteArrayInputStream(bytes)) {
            Input input = new Input(byteArrayOutputStream);
            Kryo kryo = kryoThreadLocal.get();
            Object o = kryo.readObject(input, clazz);
            //input.close();
            kryoThreadLocal.remove();
            return o;
        }
        catch (IOException e) {
            logger.error("反序列化时发生错误",e);
            return null;
        }
    }

    @Override
    public int getCode() {
        return this.code;
    }
}
