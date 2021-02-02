package entiry;

public interface RpcServer {
    public <T> void registry(Object service, String serviceName);
    public void start();
}
