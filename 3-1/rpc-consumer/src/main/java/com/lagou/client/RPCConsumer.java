package com.lagou.client;

import com.lagou.common.RpcRequest;
import com.lagou.handler.RpcClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 消费者
 */
public class RPCConsumer {

    private static ExecutorService executorService =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private static RpcClientHandler rpcClientHandler;

    public static void initClient() throws InterruptedException {
        rpcClientHandler = new RpcClientHandler();
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new RpcEncoder());
                        pipeline.addLast(new StringDecoder());
                        pipeline.addLast(rpcClientHandler);
                    }
                });
        //5)连接服务端
        bootstrap.connect("127.0.0.1", 8999).sync();
    }

    public static Object createProxy(Class<?> serviceClass) {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{serviceClass}, (obj, method, objects) -> {
                    RpcRequest rpcRequest = new RpcRequest();
                    rpcRequest.setRequestId(UUID.randomUUID().toString());
                    rpcRequest.setClassName(serviceClass.getName());
                    rpcRequest.setMethodName(method.getName());
                    rpcRequest.setParameterTypes(method.getParameterTypes());
                    rpcRequest.setParameters(objects);
                    return executorService.submit(() -> {
                        try {
                            synchronized (rpcRequest) {
                                rpcClientHandler.getRpcRequestMap().put(rpcRequest.getRequestId(), rpcRequest);
                                rpcClientHandler.getContext().writeAndFlush(rpcRequest);
                                rpcRequest.wait();
                            }
                            return rpcClientHandler.getResultMap().remove(rpcRequest.getRequestId());
                        } finally {
                            rpcClientHandler.getRpcRequestMap().remove(rpcRequest.getRequestId());
                            rpcClientHandler.getResultMap().remove(rpcRequest.getRequestId());
                        }
                    }).get();
                });
    }
}
