package com.im.server.common;

public class ProtocolConstants {

//    魔数与版本号
    public static final byte MAGIC = 0x77;

    public static final byte VERSION = 0x01;

//  序列化算法
    public static final byte JSON_ALGORITHM = 0x01;

    public static final byte PROTOBUF_ALGORITHM = 0x02;

//    数据类型，通常只有String

    public static final byte STRING_TYPE = 0x01;

    public static final byte INTEGER_TYPE = 0x02;

    public static final byte BOOLEAN_TYPE = 0x03;

    public static final byte BYTE_TYPE = 0x04;


//    指令类型

    public static final byte CONNECTION_REQUEST = 0x01;

    public static final byte CONNECTION_RESPONSE = 0x02;

    public static final byte CONNECTION_FINAL = 0x03;

    public static final byte PUBLISH_COMMAND=0x04;

//    状态位

    public static final byte DEFAULT_STATUS=0x00;


//    加密类型

        public static final byte NO_ENCRYPT = 0x00;

        public static final byte AES_ENCRYPT = 0x01;

        public static final byte DES_ENCRYPT = 0x02;


}
