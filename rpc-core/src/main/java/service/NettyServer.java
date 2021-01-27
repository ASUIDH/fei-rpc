package service;

import Provider.DefaultServiceProvider;
import Provider.ServiceProvider;
import Registry.DefaultServiceRegistry;
import Registry.ServiceRegistry;
import codec.CommonDecoder;
import codec.CommonEncoder;
import entiry.RpcServer;
import enumeration.RpcError;
import exception.RpcException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import serializer.CommonSerializer;

public class NettyServer implements RpcServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
    private CommonSerializer serializer;
    private ServiceRegistry registry;
    private ServiceProvider provider;
    private int port;
    public NettyServer(String nacosServerAddr,int port)
    {
        this.port = port;
        registry = new DefaultServiceRegistry(nacosServerAddr);
        provider = new DefaultServiceProvider();
    }
    public <T>  void registry(Object service, Class<T> serviceClass)
    {
        String name = serviceClass.getName();
        provider.registry(service);
        registry.registry(name, "127.0.0.1", this.port);
    }
    @Override
    public void start() {
        if(serializer ==null)
        {
            logger.error("序列化器未初始化");
            throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        try{
            bootstrap.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .option(ChannelOption.SO_BACKLOG, 128)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .childHandler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addLast(new CommonEncoder(serializer));//编码
                    pipeline.addLast(new CommonDecoder());//解码
                    pipeline.addLast(new NettyServerHandler());//处理
                }
            });
            ChannelFuture future = bootstrap.bind(port).sync();
            future.addListener(new ChannelFutureListener(){

                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if(channelFuture.isSuccess()) {
                        logger.info("服务开启成功");
                    }
                }
            });
            future.channel().closeFuture().sync();
        }
        catch (Exception e)
        {
            logger.error("启动链接发生错误",e);
        }
        finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
}
