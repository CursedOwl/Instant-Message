package com.im.server.processor;

import io.netty.buffer.ByteBuf;

public abstract class EncryptProcessor {
    public abstract byte[] encrypt(byte[] body);

    public abstract byte[] decrypt(ByteBuf byteBuf, int length);
}
