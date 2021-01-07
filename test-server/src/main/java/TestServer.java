import service.RpcServer;

public class TestServer {


    public static void main(String[] args) {
        RpcServer rpcServer = new RpcServer();
        rpcServer.register(new HelloServiceImpl(), 9000);

    }
}
