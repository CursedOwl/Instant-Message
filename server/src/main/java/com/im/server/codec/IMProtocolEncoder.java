package com.im.server.codec;

import com.im.server.common.ProtocolConstants;
import com.im.server.entity.IMProtocol;
import com.im.server.processor.EncryptProcessor;
import com.im.server.processor.SerializeProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

@Slf4j
public class IMProtocolEncoder extends MessageToByteEncoder<IMProtocol<Object>> {

    private final HashMap<Byte, SerializeProcessor> serializeProcessorMap;

    private final HashMap<Byte, EncryptProcessor> encryptProcessorMap;

    public IMProtocolEncoder(HashMap<Byte,EncryptProcessor> encryptProcessorMap, HashMap<Byte,SerializeProcessor> serializeProcessorMap){
        this.encryptProcessorMap = encryptProcessorMap;
        this.serializeProcessorMap = serializeProcessorMap;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, IMProtocol imProtocol, ByteBuf byteBuf) throws Exception {
        log.info(imProtocol.toString());
        byteBuf.writeByte(imProtocol.getMagic())
                .writeByte(imProtocol.getVersion())
                .writeByte(imProtocol.getSerializeAlgorithm())
                .writeByte(imProtocol.getEncrypt())
                .writeByte(imProtocol.getCommand())
                .writeByte(imProtocol.getStatus())
                .writeByte(imProtocol.getDataType());

        byte[] body;
//        Serialize and Encrypt
        SerializeProcessor serializeProcessor = serializeProcessorMap.get(imProtocol.getSerializeAlgorithm());
        switch (imProtocol.getDataType()){
            case ProtocolConstants.OBJECT_TYPE:{
                body = serializeProcessor.serialize(imProtocol.getBody());
                break;
            }
            case ProtocolConstants.INTEGER_TYPE:
            case ProtocolConstants.STRING_TYPE: {
                body=imProtocol.getBody().toString().getBytes();
                break;
            }
            default:{
                log.error("Data type is not correct");
                throw new ClassNotFoundException();
            }
        }
        if(imProtocol.getEncrypt()!= ProtocolConstants.NO_ENCRYPT){
            EncryptProcessor encryptProcessor = encryptProcessorMap.get(imProtocol.getEncrypt());
            byte[] encrypt = encryptProcessor.encrypt(body);
            byteBuf.writeInt(encrypt.length);
            byteBuf.writeBytes(encrypt);
        }else{
            log.info("No encrypt");
            byteBuf.writeInt(body.length);
            byteBuf.writeBytes(body);
        }
        channelHandlerContext.writeAndFlush(byteBuf);

    }
}
