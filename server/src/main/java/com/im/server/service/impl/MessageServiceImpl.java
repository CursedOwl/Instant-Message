package com.im.server.service.impl;

import com.google.gson.Gson;
import com.im.server.message.PublishMessage;
import com.im.server.service.MessageService;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class MessageServiceImpl implements MessageService {

    private final RestHighLevelClient restHighLevelClient=new RestHighLevelClient(RestClient.builder(
            HttpHost.create("http://123.249.39.36:9200")
    ).setRequestConfigCallback((builder)-> {
        builder.setConnectTimeout(10000)
                .setConnectionRequestTimeout(10000)
                .setSocketTimeout(10000);
        return builder;
    }));

    private final Gson gson =new Gson();

    @Override
    public void saveMessage(PublishMessage publishMessage) {
        String im = gson.toJson(publishMessage);
        IndexRequest request = new IndexRequest("im").source(im, XContentType.JSON);
        try {
            restHighLevelClient.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
