package com.im.server.factory;

import com.im.server.message.IMProtocol;

import static com.im.server.common.ProtocolConstants.*;

public class ProtocolFactory {
    public static <T> IMProtocol<T> createSimpleProtocol(T body){
        return createProtocol(body,NO_ENCRYPT,JSON_ALGORITHM,PUBLISH_COMMAND,DEFAULT_STATUS,OBJECT_TYPE);
    }

    public static <T> IMProtocol<T> createPublishProtocol(T body,byte encrypt,byte serializeAlgorithm) {
        return createProtocol(body,encrypt,serializeAlgorithm,PUBLISH_COMMAND,DEFAULT_STATUS,OBJECT_TYPE);
    }
    public static <T> IMProtocol<T> createAesProtobufProtocol(T body,byte command,byte status) {
        return createProtocol(body,AES_ENCRYPT,PROTOBUF_ALGORITHM,command,status,OBJECT_TYPE);
    }

    public static <T> IMProtocol<T> createNoEncJsonProtocol(T body,byte command,byte status) {
        return createProtocol(body,NO_ENCRYPT,JSON_ALGORITHM,command,status,OBJECT_TYPE);
    }

    public static <T> IMProtocol<T> createProtocol(T body,byte encrypt,byte serializeAlgorithm,byte command,byte status,byte dataType) {
        IMProtocol<T> imProtocol = new IMProtocol<>();
        imProtocol.setMagic(MAGIC);
        imProtocol.setVersion(VERSION);
        imProtocol.setEncrypt(encrypt);
        imProtocol.setSerializeAlgorithm(serializeAlgorithm);
        imProtocol.setCommand(command);
        imProtocol.setStatus(status);
        imProtocol.setBody(body);
        imProtocol.setDataType(dataType);

        return imProtocol;
    }



}
