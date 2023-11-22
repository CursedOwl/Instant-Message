package com.im.server;

import com.google.gson.Gson;
import com.im.server.codec.IMProtocolCodec;
import com.im.server.common.ProtocolConstants;
import com.im.server.handler.ClientInboundHandler;
import com.im.server.message.ConnectionRequest;
import com.im.server.message.IMProtocol;
import com.im.server.factory.ProtocolFactory;
import com.im.server.processor.EncryptProcessor;
import com.im.server.processor.SerializeProcessor;
import com.im.server.processor.impl.AESEncryptor;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.HashMap;

class ServerApplicationTests {

    @Test
    void contextLoads() throws InterruptedException, IOException {
        Gson gson=new Gson();
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                // 指定要使用的Channel实现类
                .channel(NioSocketChannel.class)
                // 设置Channel选项
                .option(ChannelOption.TCP_NODELAY, true)
                // 设置Channel处理器
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ClientInboundHandler());
                    }
                });

        // 连接到服务器
        Channel future = bootstrap.connect("localhost", 8081).sync().channel();

        // 发送消息到服务器
        ByteBuf buf = UnpooledByteBufAllocator.DEFAULT.buffer();
//        MAGIC-VERSION-SERIALIZE-ENCRYPT-COMMAND-STATUS-TYPE
        buf.writeBytes(new byte[]{0x77,0x01,0x01,0x00,0x01,0x01,0x00});
//        写入正文长度与内容
        ConnectionRequest connectionRequest = new ConnectionRequest(123456L,"123456",null);
        String content = gson.toJson(connectionRequest);
        buf.writeInt(content.getBytes().length);
        buf.writeBytes(content.getBytes());
        future.writeAndFlush(buf);

        // 等待连接关闭
        future.closeFuture().sync();

    }


    private final HashMap<Byte, EncryptProcessor> encryptProcessorMap=new HashMap<>();

    private final HashMap<Byte, SerializeProcessor> serializeProcessorMap= new HashMap<>();
    @Test
    public void contextLoads3(){
        encryptProcessorMap.put(ProtocolConstants.AES_ENCRYPT,new AESEncryptor());
        EmbeddedChannel channel = new EmbeddedChannel(
                new IMProtocolCodec(encryptProcessorMap, serializeProcessorMap));
//                new IMProtocolInboundHandler(null,null,null,null));

        ByteBuf byteBuf = Unpooled.copiedBuffer(new byte[]{0x77, 0x22});
        channel.writeInbound(byteBuf);

        IMProtocol<String> versionIsNotCorrect = ProtocolFactory.createProtocol("Version is not correct"
                , ProtocolConstants.NO_ENCRYPT, ProtocolConstants.JSON_ALGORITHM
                , ProtocolConstants.CHECK_COMMAND, ProtocolConstants.FAIL_STATUS, ProtocolConstants.STRING_TYPE);
        versionIsNotCorrect.setDataType(ProtocolConstants.STRING_TYPE);
        channel.writeOutbound(versionIsNotCorrect);
    }

    @Test
    public void test(){

    }

}
