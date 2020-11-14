package com.lagou.boot;

import com.lagou.handler.RpcServerHandler;
import com.lagou.service.IUserService;
import com.lagou.service.UserServiceImpl;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

public class ServerBoot {

    private static RpcServerHandler rpcServerHandler = new RpcServerHandler();

    public static void main(String[] args) throws InterruptedException {
        rpcServerHandler.getMap().put(IUserService.class.getName(), new UserServiceImpl());
        startServer(8999);
    }

    public static void startServer(int port) throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        ChannelPipeline pipeline = nioSocketChannel.pipeline();
                        pipeline.addLast(new StringEncoder());
                        pipeline.addLast(new RpcDecoder());
                        pipeline.addLast(rpcServerHandler);
                    }
                });
        serverBootstrap.bind(port).sync();
    }
}
