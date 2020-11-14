package com.lagou.handler;

import com.lagou.common.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义的业务处理器
 */
public class RpcServerHandler extends ChannelInboundHandlerAdapter {
    Map<String, Object> map = new HashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcRequest rpcRequest = (RpcRequest) msg;
        String className = rpcRequest.getClassName();
        Object o = map.get(className);
        if (o == null) {
            ctx.writeAndFlush(rpcRequest.getRequestId() + "::" + "fail");
            return;
        }
        Method method = o.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
        Object value = method.invoke(o, rpcRequest.getParameters());
        ctx.writeAndFlush(rpcRequest.getRequestId() + "::" + value.toString());
    }

    public Map<String, Object> getMap() {
        return map;
    }
}
