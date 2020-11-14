package com.lagou.handler;

import com.lagou.common.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 自定义事件处理器
 */
public class RpcClientHandler extends ChannelInboundHandlerAdapter {

    private ChannelHandlerContext context;
    private Map<String, RpcRequest> rpcRequestMap = new ConcurrentHashMap<>();
    private Map<String, Object> resultMap = new ConcurrentHashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.context = ctx;
    }

    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String s = msg.toString();
        String[] split = s.split("::");
        resultMap.put(split[0], split[1]);
        RpcRequest rpcRequest = rpcRequestMap.get(split[0]);
        synchronized (rpcRequest) {
            rpcRequest.notify();
        }
    }


    public Map<String, RpcRequest> getRpcRequestMap() {
        return rpcRequestMap;
    }

    public ChannelHandlerContext getContext() {
        return context;
    }

    public Map<String, Object> getResultMap() {
        return resultMap;
    }
}
