package client;

import codec.CommonDecoder;
import codec.CommonEncoder;
import entiry.RpcClient;
import entiry.RpcRequest;
import entiry.RpcResponse;
import enumeration.RpcError;
import exception.RpcException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.socket.SocketChannel;
import serializer.CommonSerializer;
import serializer.JsonSerializer;
import util.RpcMessageChecker;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class NettyClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private int port;
    private String host;
    private static Bootstrap bootstrap;
    private CommonSerializer serializer;
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
                .option(ChannelOption.SO_KEEPALIVE, true);
    }
    @Override
    public Object sendRequest(RpcRequest rpcRequest)  {
        if(serializer == null) {
            logger.error("未初始化序列化方式");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline pipeline = socketChannel.pipeline();
                pipeline.addLast(new CommonDecoder())
                .addLast(new CommonEncoder(serializer))
                .addLast(new NettyClientHandler());
            }
        });
        try {
            Channel channel = ChannelProvider.get(new InetSocketAddress(host,port),CommonSerializer.getByCode(1));
            Object ans = null;
            if(channel!=null && channel.isActive()) {
                channel.writeAndFlush(rpcRequest).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        if (channelFuture.isSuccess()) {
                            logger.info("客户端发送消息恒功 :" + rpcRequest.toString());
                        } else {
                            logger.error("客户端发送消息失败");
                        }
                    }
                });
                channel.closeFuture().sync();//这个才是同步吧，我关了才去取
                AttributeKey<RpcResponse> attributeKey = AttributeKey.valueOf("response" + rpcRequest.getRequestId());
                RpcResponse response = channel.attr(attributeKey).get();
                RpcMessageChecker.check(rpcRequest, response);
                return response.getData();
            }
            else{
                logger.error("连接建立失败");
                System.exit(0);
                return null; //加上这个才能通过编译
            }
        }
        catch (InterruptedException e) {
            logger.error("建立连接发生错误");
            return null;
        }
    }

    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
}
