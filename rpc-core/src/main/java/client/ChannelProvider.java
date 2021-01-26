package client;

import codec.CommonDecoder;
import codec.CommonEncoder;
import enumeration.RpcError;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import serializer.CommonSerializer;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ChannelProvider {
    private static final Logger logger = LoggerFactory.getLogger(ChannelProvider.class);
    private static Bootstrap bootstrap = iniBootstrap();
    private static final int MAX_RETRY = 3;
    private static Channel channel; //这个玩意明显线程不安全,所以在get前面加上synchronized关键字（后续考虑进一步更改，channel不应该作为一个静态变量出现。）
    public static synchronized Channel get (InetSocketAddress inetSocketAddress, CommonSerializer serializer) throws InterruptedException
    {
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline pipline = socketChannel.pipeline();
                pipline.addLast(new CommonEncoder(serializer));
                pipline.addLast(new CommonDecoder());
                pipline.addLast(new NettyClientHandler());
            }
        });
        CountDownLatch countDownLatch = new CountDownLatch(1);
        connect(bootstrap,inetSocketAddress,countDownLatch);
        return  channel;
    }
    private static void connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress, CountDownLatch countDownLatch) throws InterruptedException {
        connect(bootstrap, inetSocketAddress, countDownLatch,MAX_RETRY);
        countDownLatch.await(); //不会无限等
    }
    private static void connect(Bootstrap bootstrap ,InetSocketAddress inetSocketAddress,CountDownLatch countDownLatch,int retry_times)
    {
        bootstrap.connect(inetSocketAddress).addListener(new ChannelFutureListener()
        {

            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if(channelFuture.isSuccess())
                {
                    channel = channelFuture.channel();
                    countDownLatch.countDown();
                }
                else if(retry_times>0)
                {
                    int count = MAX_RETRY - retry_times;
                    int waitTime = 1 << count;
                    logger.error("{}:连接失败，第{}次重连",new Date(),count+1);
                    channelFuture.channel().eventLoop().schedule(new Runnable() {
                        @Override
                        public void run() {
                            connect(bootstrap, inetSocketAddress, countDownLatch,retry_times-1);
                        }
                    },waitTime,TimeUnit.SECONDS);
                }
                else
                {
                    logger.error("建立连接失败");
                    countDownLatch.countDown();
                    throw new RuntimeException(RpcError.CLIENT_CONNECT_SERVER_FAILURE.getMessage());
                }
            }
        });
    }
    private static Bootstrap iniBootstrap()
    {
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000 )
                .option(ChannelOption.SO_KEEPALIVE,true )
                .option(ChannelOption.TCP_NODELAY,false);//true开启Nagle算法，会导致沾包。
        return bootstrap;
    }
}
