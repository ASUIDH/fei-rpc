package loadbalancer;

import java.util.List;

public interface LoadBalancer<T> {
    T select(List<T> instances);
    int getCode();
    static <T> LoadBalancer<T> getByCode(int code)
    {
        switch (code){
            case 0:
                return new RandomLoadBalancer<T>();
            case 1:
                return new RoundRobinLoadBalancer();
            default:
                return null;
        }
    }
}
