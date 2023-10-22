package com.im.server;

import com.im.server.codec.IMProtocolCodec;
import com.im.server.common.ProtocolConstants;
import com.im.server.message.IMProtocol;
import com.im.server.factory.ProtocolFactory;
import com.im.server.processor.EncryptProcessor;
import com.im.server.processor.SerializeProcessor;
import com.im.server.processor.impl.AESEncryptor;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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
        ByteBuf buf = Unpooled.copiedBuffer(new byte[]{0x77,0x22});
        future.writeAndFlush(buf);
        Thread.sleep(1000);
        ByteBuf buf2 = Unpooled.copiedBuffer(new byte[]{0x77,0x22});
        future.writeAndFlush(buf2);
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
