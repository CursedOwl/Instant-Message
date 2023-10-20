package com.im.server.service;


import com.im.server.message.PublishMessage;

public interface KafkaService {
    public void sendPublish(String topic, PublishMessage publishMessage);
}
