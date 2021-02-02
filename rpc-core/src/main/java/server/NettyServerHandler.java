package server;

import entiry.RpcRequest;
import entiry.RpcResponse;
import factory.SingletonFactory;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import Provider.DefaultServiceProvider;
import Provider.ServiceProvider;

public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private final static Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private static RequestHandler requestHandler;
    private static ServiceProvider serviceProvider;
    static {
        serviceProvider = new DefaultServiceProvider();
    }
    public NettyServerHandler(){
        this.requestHandler = SingletonFactory.getInstance(RequestHandler.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        try {
            //这里不想用线程池,netty本来就多线程来处理事件,再启动更多的线程是否有意义,这里应当更多了解netty线程模型再作结论?
            String interfaceName = rpcRequest.getInterfaceName();
            Object service = serviceProvider.getService(interfaceName);
            Object obj = requestHandler.handle(rpcRequest,service);
            ChannelFuture future = channelHandlerContext.writeAndFlush(RpcResponse.success(obj,rpcRequest.getRequestId()));//其实这里有问题啊，还是返回不了fail信息，后面整体该把
            future.addListener(ChannelFutureListener.CLOSE);//执行完了就把future给close了，一个rpc留着链接有什么用呢？(前面为什么不用短链接)
        }
        finally {
            ReferenceCountUtil.release(rpcRequest);
            //注意由解码器（decoder）生成的消息（messages）对象，很可能是引用计数对象（很可能？），引用计数对象释放，gc不管自己管吧
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("客户端地址为 : " + ctx.channel().remoteAddress().toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("处理过程调用时有错误发生:",cause);
        cause.printStackTrace();
        ctx.close(); //异常了把Context关了，高明啊
    }
}
