package com.im.server.factory;

import com.im.server.entity.IMProtocol;

import static com.im.server.common.ProtocolConstants.*;

public class ProtocolFactory {
    public static <T> IMProtocol<T> createSimpleProtocol(T body){
        return createProtocol(body,AES_ENCRYPT,PROTOBUF_ALGORITHM,PUBLISH_COMMAND,DEFAULT_STATUS);
    }

    public static <T> IMProtocol<T> createPublishProtocol(T body,byte encrypt,byte serializeAlgorithm) {
        return createProtocol(body,encrypt,serializeAlgorithm,PUBLISH_COMMAND,DEFAULT_STATUS);
    }
    public static <T> IMProtocol<T> createAesProtobufProtocol(T body,byte command,byte status) {
        return createProtocol(body,AES_ENCRYPT,PROTOBUF_ALGORITHM,command,status);
    }

    public static <T> IMProtocol<T> createProtocol(T body,byte encrypt,byte serializeAlgorithm,byte command,byte status) {
        return null;
    }



}
