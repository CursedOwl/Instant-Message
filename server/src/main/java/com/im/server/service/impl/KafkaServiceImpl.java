package com.im.server.service.impl;

import com.im.server.message.PublishMessage;
import com.im.server.service.KafkaService;
import org.springframework.stereotype.Service;

@Service
public class KafkaServiceImpl implements KafkaService {


//    TODO 完成向Kafka发送具体消息，然后由Flink对其Datasource进行监听，监听后对数据流进行特定解析然后存入ES内
    @Override
    public void sendPublish(String topic, PublishMessage publishMessage) {

    }
}
