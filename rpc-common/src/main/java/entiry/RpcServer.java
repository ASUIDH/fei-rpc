package entiry;

public interface RpcServer {
    public <T> void registry(Object service, Class<T> serviceClass);
    public void start();
}
