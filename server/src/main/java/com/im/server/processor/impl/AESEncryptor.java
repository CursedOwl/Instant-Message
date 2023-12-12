package com.im.server.processor.impl;

import com.im.server.processor.EncryptProcessor;
import io.netty.buffer.ByteBuf;

public class AESEncryptor extends EncryptProcessor {
    @Override
    public byte[] encrypt(byte[] body) {
        return new byte[0];
    }

    @Override
    public byte[] decrypt(ByteBuf byteBuf, int length) {
        return new byte[0];
    }

    @Override
    public byte[] decrypt(byte[] body) {
        return new byte[0];
    }


}
