package com.im.server.codec;

import com.im.server.common.ProtocolConstants;
import com.im.server.message.IMProtocol;
import com.im.server.factory.ProtocolFactory;
import com.im.server.message.ConnectionRequest;
import com.im.server.message.PublishMessage;
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
public class IMProtocolCodec extends ByteToMessageCodec<IMProtocol<Object>> {

    private IMProtocol<Object> imMessage;

    private Integer badMessageCount=0;

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
        READ_BODY,
        BAD_MESSAGE
    }

    public IMProtocolCodec(HashMap<Byte,EncryptProcessor> encryptProcessorMap, HashMap<Byte,SerializeProcessor> serializeProcessorMap){
        this.encryptProcessorMap = encryptProcessorMap;
        this.serializeProcessorMap = serializeProcessorMap;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, IMProtocol imProtocol, ByteBuf byteBuf) throws Exception {
        log.info(imProtocol.toString());
        byteBuf.writeByte(ProtocolConstants.MAGIC)
                .writeByte(ProtocolConstants.VERSION)
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
//            log.info("No encrypt");
            byteBuf.writeInt(body.length);
            byteBuf.writeBytes(body);
        }
        //        channelHandlerContext.write(byteBuf);
//        channelHandlerContext.writeAndFlush(byteBuf);
    }

    @Override

    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if(resetRequest.get()){
            resetNow();
        }
        log.info("["+channelHandlerContext.channel()+"]Remains:"+byteBuf.readableBytes());
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
                   log.error("Version is not correct:["+String.format("0x%02x", temp)+"]");
                   IMProtocol<String> versionIsNotCorrect = ProtocolFactory.createProtocol("Version is not correct"
                           , ProtocolConstants.NO_ENCRYPT, ProtocolConstants.JSON_ALGORITHM
                           , ProtocolConstants.CHECK_COMMAND, ProtocolConstants.FAIL_STATUS, ProtocolConstants.STRING_TYPE);
                   versionIsNotCorrect.setDataType(ProtocolConstants.STRING_TYPE);
//                   channelHandlerContext.channel().writeAndFlush(versionIsNotCorrect);
                   list.add(versionIsNotCorrect);
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
                imMessage.setSerializeAlgorithm(temp);
                currentState=State.READ_ENCRYPT;
            }

            case READ_ENCRYPT:{
                temp=byteBuf.readByte();
                if(temp!=ProtocolConstants.AES_ENCRYPT&&temp!=ProtocolConstants.DES_ENCRYPT&&temp!=ProtocolConstants.NO_ENCRYPT){
                    log.error("Encrypt algorithm is not correct"+String.format("0x%02x", temp)+"]");
                    currentState=State.BAD_MESSAGE;
                    return;
                }
                imMessage.setEncrypt(temp);
                currentState=State.READ_COMMAND;
            }

            case READ_COMMAND:{
                imMessage.setCommand(byteBuf.readByte());
                currentState=State.READ_STATUS;
            }

            case READ_STATUS:{
                imMessage.setStatus(byteBuf.readByte());
                currentState=State.READ_DATA_TYPE;
            }

            case READ_DATA_TYPE:{
                temp=byteBuf.readByte();
                switch (temp){
                    case ProtocolConstants.STRING_TYPE:
                    case ProtocolConstants.OBJECT_TYPE:{
                        imMessage.setDataType(temp);
                        break;
                    }
                    case ProtocolConstants.INTEGER_TYPE:{
                        byteBuf.skipBytes(8);
                        currentState=State.READ_MAGIC;
                        return;
                    }
                    case ProtocolConstants.BYTE_TYPE:
                    case ProtocolConstants.BOOLEAN_TYPE:{
                        byteBuf.skipBytes(5);
                        currentState=State.READ_MAGIC;
                        return;
                    }

                    default:{
                        log.error("Data type is not correct"+String.format("0x%02x", temp)+"]");
                        currentState=State.BAD_MESSAGE;
                        return;
                    }
                }
                currentState=State.READ_BODY;
            }

            case READ_BODY:{
                int length = byteBuf.readInt();
//                如果存在网络延迟，会导致半包，需要等待
                if(byteBuf.readableBytes()<length){
                    byteBuf.resetReaderIndex();
                    currentState=State.READ_MAGIC;
                    resetRequest.set(true);
                    return;
                }
                byte[] body = new byte[length];
                byteBuf.readBytes(body);
                EncryptProcessor encryptProcessor = encryptProcessorMap.get(imMessage.getEncrypt());
                SerializeProcessor serializeProcessor = serializeProcessorMap.get(imMessage.getSerializeAlgorithm());
                switch (imMessage.getCommand()){
                    case ProtocolConstants.CONNECTION_COMMAND:{
                        imMessage.setClazz(ConnectionRequest.class);
                        imMessage.setBody(serializeProcessor.deserialize(encryptProcessor.decrypt(body), ConnectionRequest.class));
                        list.add(imMessage);
                        resetNow();
                        break;
                    }
                    case ProtocolConstants.PUBLISH_COMMAND:{
                        imMessage.setClazz(PublishMessage.class);
                        imMessage.setBody(serializeProcessor.deserialize(encryptProcessor.decrypt(body), PublishMessage.class));
                        list.add(imMessage);
                        resetNow();
                        break;
                    }

                }
                break;
            }

            case BAD_MESSAGE:{
                byteBuf.skipBytes(byteBuf.readableBytes());
                badMessageCount+=1;
                resetRequest.set(true);
                list.add(ProtocolFactory.createNoEncJsonProtocol("Bad Message"
                        , ProtocolConstants.BAD_MESSAGE_COMMAND, ProtocolConstants.FAIL_STATUS));
                if (badMessageCount>5){
                    channelHandlerContext.close();
                }
                break;
            }
       }
    }

    private void resetNow() {
        resetRequest.lazySet(true);
        currentState=State.READ_MAGIC;
        imMessage=null;
    }


}
