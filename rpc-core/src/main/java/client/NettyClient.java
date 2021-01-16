package client;

import codec.CommonDecoder;
import codec.CommonEncoder;
import entiry.RpcClient;
import entiry.RpcRequest;
import entiry.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.socket.SocketChannel;
import serializer.CommonSerializer;
import serializer.JsonSerializer;

public class NettyClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private int port;
    private String host;
    private static Bootstrap bootstrap;
    public NettyClient(String host,int port)
    {
        this.host = host;
        this.port = port;
    }
    static {
        bootstrap = new Bootstrap();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        bootstrap.group(workGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new CommonEncoder(CommonSerializer.getByCode(0)))
                                .addLast(new CommonDecoder())
                                .addLast(new NettyClientHandler());
                    }
                });
    }
    @Override
    public Object sendRequest(RpcRequest rpcRequest)  {
        try {
            ChannelFuture cF = bootstrap.connect(host, port).sync();
            Object ans = null;
            cF.channel().writeAndFlush(rpcRequest).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if(channelFuture.isSuccess()) {
                        logger.info("客户端发送消息恒功 :"+rpcRequest.toString());
                    }
                    else {
                        logger.error("客户端发送消息失败");
                    }
                }
            });
            cF.channel().closeFuture().sync();//这个才是同步吧，我关了才去取
            AttributeKey <RpcResponse> attributeKey =  AttributeKey.valueOf("response");
            RpcResponse response = cF.channel().attr(attributeKey).get();
            return response.getData();
        }
        catch (InterruptedException e) {
            logger.error("建立连接发生错误");
            return null;
        }
    }
}
