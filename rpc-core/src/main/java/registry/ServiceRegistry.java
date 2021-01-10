package registry;

public interface ServiceRegistry {
    <T> void registry(T service);
    public Object getService(String interfaceName);
}
