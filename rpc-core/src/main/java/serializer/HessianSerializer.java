package serializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import enumeration.RpcError;
import exception.RpcException;
import exception.SerializeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HessianSerializer implements CommonSerializer {
    private static final Logger logger = LoggerFactory.getLogger(HessianSerializer.class);
    private static final int CODE =2;
    @Override
    public byte[] serialize(Object obj) {
        HessianOutput hessianOutput = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            hessianOutput = new HessianOutput(byteArrayOutputStream);
            hessianOutput.writeObject(obj);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            logger.error("序列化发生错误", e);
            throw new SerializeException("序列化发生错误");
        }
        finally {
            if(hessianOutput!=null) {
                try{
                    hessianOutput.close();
                }
                catch (IOException e) {
                    logger.error("关闭流发生问题", e);
                }
            }
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        HessianInput hessianInput = null;
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            hessianInput = new HessianInput(byteArrayInputStream);
            Object object =hessianInput.readObject(clazz);
            return object;
        } catch (IOException e) {
            logger.error("反序列化发生错误", e);
            throw new SerializeException("反序列化发生错误");
        }
        finally {
            if (hessianInput != null) {
                    hessianInput.close();
            }
        }
    }

    @Override
    public int getCode() {
        return CODE;
    }
}
