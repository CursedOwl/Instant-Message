package com.im.server.processor;

public abstract class SerializeProcessor {

        public abstract byte[] serialize(Object object);

        public abstract Object deserialize(byte[] bytes, Class<?> clazz);
}
