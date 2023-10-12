package com.im.server.server;

import com.im.server.codec.IMProtocolCodec;
import com.im.server.common.ProtocolConstants;
import com.im.server.handler.IMProtocolInboundHandler;
import com.im.server.processor.EncryptProcessor;
import com.im.server.processor.SerializeProcessor;
import com.im.server.processor.impl.AESEncryptor;
import com.im.server.service.UserService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Slf4j
@Component
public class NettyServer {

    private final EventLoopGroup bossGroup=new NioEventLoopGroup(1);
    private final EventLoopGroup workerGroup=new NioEventLoopGroup();

    private final HashMap<Byte, EncryptProcessor> encryptProcessorMap;

    private final HashMap<Byte, SerializeProcessor> serializeProcessorMap;

    @Autowired
    private UserService userService;

    public NettyServer(){
        this.encryptProcessorMap = new HashMap<>();
        this.serializeProcessorMap = new HashMap<>();
        encryptProcessorMap.put(ProtocolConstants.AES_ENCRYPT,new AESEncryptor());
    }

    public void launch(int port) {
        try {
            ChannelFuture channelFuture = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            nioSocketChannel.pipeline()
//                                    .addLast(new IMProtocolOutbound(encryptProcessorMap,serializeProcessorMap))
                                    .addLast(new IMProtocolCodec(encryptProcessorMap,serializeProcessorMap))
                                    .addLast(new IMProtocolInboundHandler(userService));

                        }
                    }).bind(port);

            channelFuture.sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            log.info("Netty Server close");
        }
    }
}
