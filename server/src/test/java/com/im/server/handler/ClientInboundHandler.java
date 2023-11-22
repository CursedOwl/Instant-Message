package com.im.server.handler;

import com.google.gson.Gson;
import com.im.server.message.ConnectionCallback;
import com.im.server.message.ConnectionRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ClientInboundHandler extends ByteToMessageDecoder {

    private final Gson gson=new Gson();

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        System.out.println("[MAGIC]:"+byteBuf.readByte());
        System.out.println("[VERSION]:"+byteBuf.readByte());
        System.out.println("[SERIALIZE]:"+byteBuf.readByte());
        System.out.println("[ENCRYPT]:"+byteBuf.readByte());
        System.out.println("[COMMAND]:"+byteBuf.readByte());
        System.out.println("[STATUS]:"+byteBuf.readByte());
        System.out.println("[TYPE]:"+byteBuf.readByte());
        int length = byteBuf.readInt();
        byte[] bytes = new byte[length];
        System.out.println("[LENGTH]:"+length);
        byteBuf.readBytes(bytes);
        String content = new String(bytes);
        ConnectionCallback cb = gson.fromJson(content, ConnectionCallback.class);
        System.out.println("[TOKEN]:"+cb.getMessage());

    }
}
