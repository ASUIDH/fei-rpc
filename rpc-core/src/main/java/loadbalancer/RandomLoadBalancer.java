package loadbalancer;

import java.util.List;
import java.util.Random;

public class RandomLoadBalancer<T> implements LoadBalancer<T> {

    @Override
    public T select(List<T> instances) {
        return instances.get(new Random().nextInt(instances.size()));
    }

    @Override
    public int getCode() {
        return 0;
    }
}
