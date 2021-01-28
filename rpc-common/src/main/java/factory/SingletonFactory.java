package factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SingletonFactory {

    private static Map<Class,Object> map = new ConcurrentHashMap<>();
    private SingletonFactory(){}
    public static <T> T getInstance (Class<T> clazz)
    {
        Object instance = map.get(clazz);
        if(instance==null)
        {
            synchronized (clazz)
            {
                if(instance == null)
                {
                    try {
                        instance = clazz.newInstance();
                        map.put(clazz, instance);
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return clazz.cast(instance);
    }
}
