package loadbalancer;

import java.util.List;

public class RoundRobinLoadBalancer<T> implements LoadBalancer<T> {
    int pos = 0;
    @Override
    public T select(List<T> instances) {
        if(pos >= instances.size())
        {
            pos = 0;
        }
        return instances.get(pos++);
    }

    @Override
    public int getCode() {
        return 1;
    }
}
