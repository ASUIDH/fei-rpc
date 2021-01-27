package Provider;

public interface ServiceProvider {
    <T> void registry(T service);
    public Object getService(String interfaceName);
}
