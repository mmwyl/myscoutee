package com.raxim.myscoutee.profile.service;

import com.raxim.myscoutee.common.config.FirebaseProperties;
import com.raxim.myscoutee.profile.message.Notification;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.message.BasicHeader;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

public class FirebaseService {

    private final FirebaseProperties firebaseProperties;

    public FirebaseService(FirebaseProperties firebaseProperties) {
        this.firebaseProperties = firebaseProperties;
    }

    public Future<HttpResponse> sendAsync(Notification notification) throws Exception {
        HttpPost httpPost = new HttpPost(firebaseProperties.getUrl());
        Map<String, String> headers = new HashMap<>();

        headers.put("Authorization", "key=" + firebaseProperties.getApiKey());
        headers.put("Content-Type", "application/json");
        httpPost.setEntity(new ByteArrayEntity(null));

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            httpPost.addHeader(new BasicHeader(entry.getKey(), entry.getValue()));
        }

        CloseableHttpAsyncClient closeableHttpAsyncClient = HttpAsyncClients.createDefault();
        closeableHttpAsyncClient.start();
        return closeableHttpAsyncClient.execute(httpPost, new ClosableCallback(closeableHttpAsyncClient));
    }
}
