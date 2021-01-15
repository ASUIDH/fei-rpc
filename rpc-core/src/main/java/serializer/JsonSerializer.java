package serializer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import entiry.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JsonSerializer implements CommonSerializer {
    Logger  logger = LoggerFactory.getLogger(JsonSerializer.class);
    private ObjectMapper objectMapper = new ObjectMapper();
    int code = 1;
    @Override
    public byte[] serialize(Object obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        }
        catch (JsonProcessingException e) {
            logger.error("序列化发生问题",e);
            return null;
        }
    }

    @Override
    public Object deserialize(byte[] bytes,Class<?> clazz) {

        try {
            Object obj = objectMapper.readValue(bytes,clazz);
            if(obj instanceof RpcRequest)
            {
                obj = handleRequest(obj);
            }
            return obj;
        } catch (IOException e) {
            logger.error("反序列化发生问题",e);
            return null;
        }
    }
    private Object handleRequest(Object obj) throws IOException {
        RpcRequest rpcRequest = (RpcRequest)obj;
        for(int i = 0 ; i < rpcRequest.getParameters().length;i++)
        {
            Class<?> clazz = rpcRequest.getParamTypes()[i];
            if(!clazz.isAssignableFrom(rpcRequest.getParameters()[i].getClass()))
            {
                byte[] bytes = objectMapper.writeValueAsBytes(rpcRequest.getParameters()[i]);
                rpcRequest.getParameters()[i] = objectMapper.readValue(bytes,clazz);
            }
        }
        return rpcRequest;
    }
    @Override
    public int getCode() {
        return this.code;
    }
}
