package serializer;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ProtobufSerializer implements CommonSerializer {
    private static LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
    private static Map<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<Class<?>, Schema<?>>();
    @Override
    public byte[] serialize(Object obj) {
        Schema schema = getSchema(obj.getClass());
        byte[] data;
        try{
            data = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        }
        finally {
            buffer.clear();
        }
        return data;
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        Schema schema = getSchema(clazz);
        Object result = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes,result,schema);
        return result;
    }

    @Override
    public int getCode() {
        return 3;
    }
    private static <T> Schema<T> getSchema(Class<T> clazz)
    {
        Schema<T> schema = (Schema<T>)(schemaCache.get(clazz));
        if(Objects.isNull(schema))
        {
            schema = RuntimeSchema.getSchema(clazz);
            if(Objects.nonNull(schema))
            {
                schemaCache.put(clazz, schema);
            }
        }
        return schema;
    }
}
