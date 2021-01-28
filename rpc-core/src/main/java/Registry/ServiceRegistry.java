package Registry;

import java.net.InetSocketAddress;

public interface ServiceRegistry {
    void registry(String serviceName,String host, int port);

}
