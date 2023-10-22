package com.im.server.processor.impl;

import com.google.gson.Gson;
import com.im.server.processor.SerializeProcessor;

public class JsonSerializer extends SerializeProcessor {
    
    private final Gson gson = new Gson();
    
    @Override
    public byte[] serialize(Object object) {
        return gson.toJson(object).getBytes();
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        return gson.fromJson(new String(bytes), clazz);
    }
}
