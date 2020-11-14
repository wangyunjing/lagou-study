package com.lagou.boot;

import com.alibaba.fastjson.JSON;
import com.lagou.common.RpcRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author yunjing.wang
 * @date 2020/11/14
 */
public class RpcDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() >= 4) {
            int lenght = in.readInt();
            byte[] bytes = new byte[lenght];
            in.readBytes(bytes,0, lenght);
            Object object = JSON.parseObject(bytes, RpcRequest.class);
            out.add(object);
        }
    }
}
