package com.im.server.codec;

import com.im.server.common.ProtocolConstants;
import com.im.server.entity.IMProtocol;
import com.im.server.processor.EncryptProcessor;
import com.im.server.processor.SerializeProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

// 作为自定义协议的decode与encode
@Slf4j
public class IMProtocolCodec extends ByteToMessageCodec<IMProtocol>  {

    private IMProtocol imProtocol;

    private Integer badMessageCount=0;

    private byte serializeType;

    private byte encryptType;


    private final HashMap<Byte, SerializeProcessor> serializeProcessorMap;

    private final HashMap<Byte, EncryptProcessor> encryptProcessorMap;

    private AtomicBoolean resetRequest= new AtomicBoolean(false);

    private State currentState=State.READ_MAGIC;

    private enum State{
        READ_MAGIC,
        READ_VERSION,
        READ_SERIALIZE_ALGORITHM,
        READ_ENCRYPT,
        READ_COMMAND,
        READ_STATUS,
        READ_DATA_TYPE,
        READ_LENGTH,
        READ_BODY,
        BAD_MESSAGE
    }

    public IMProtocolCodec(HashMap<Byte,EncryptProcessor> encryptProcessorMap,HashMap<Byte,SerializeProcessor> serializeProcessorMap){
        this.encryptProcessorMap = encryptProcessorMap;
        this.serializeProcessorMap = serializeProcessorMap;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, IMProtocol imProtocol, ByteBuf byteBuf) throws Exception {
        byteBuf.writeByte(imProtocol.getMagic())
                .writeByte(imProtocol.getVersion())
                .writeByte(imProtocol.getSerializeAlgorithm())
                .writeByte(imProtocol.getEncrypt())
                .writeByte(imProtocol.getCommand())
                .writeByte(imProtocol.getStatus())
                .writeByte(imProtocol.getDataType())
                .writeInt(imProtocol.getLength())
                .writeBytes(imProtocol.getBody());

        channelHandlerContext.writeAndFlush(byteBuf);
    }

    @Override

    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
//        Thread.sleep(500);
//        log.info("["+channelHandlerContext.channel()+"]Remains:"+byteBuf.readableBytes());
        byte temp;
        switch (currentState){
           case READ_MAGIC:{
               if ((temp=byteBuf.readByte()) != ProtocolConstants.MAGIC) {
                   log.error("Magic is not correct:["+String.format("0x%02x", temp)+"]");
                   currentState=State.BAD_MESSAGE;
                   return;
               }
               currentState=State.READ_VERSION;
           }
           case READ_VERSION:{
               if ((temp=byteBuf.readByte()) != ProtocolConstants.VERSION) {
                   log.error("Version is not correct"+String.format("0x%02x", temp)+"]");
                   currentState=State.BAD_MESSAGE;
                   return;
               }
               currentState=State.READ_SERIALIZE_ALGORITHM;
           }

            case READ_SERIALIZE_ALGORITHM:{
                temp=byteBuf.readByte();
                if(temp!=ProtocolConstants.JSON_ALGORITHM && temp!=ProtocolConstants.PROTOBUF_ALGORITHM){
                    log.error("Serialize algorithm is not correct"+String.format("0x%02x", temp)+"]");
                    currentState=State.BAD_MESSAGE;
                    return;
                }
                serializeType=temp;
                currentState=State.READ_ENCRYPT;
            }

            case BAD_MESSAGE:{
                byteBuf.skipBytes(byteBuf.readableBytes());
                badMessageCount+=1;
                if (badMessageCount>5){
                    channelHandlerContext.close();
                }
                break;
            }
       }
    }
    public IMProtocol buildResponse(){
        return new IMProtocol();
    }

}
