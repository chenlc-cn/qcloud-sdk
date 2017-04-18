/*
 * Copyright 2017-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.chenlc.qcloud.sdk.common.http;

import cn.chenlc.qcloud.sdk.common.exceptions.ParamException;
import cn.chenlc.qcloud.sdk.common.exceptions.QcloudSdkException;
import org.apache.http.HttpMessage;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.IdleConnectionEvictor;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 个性化HTTP客户端抽象封装
 *
 * @author chenlc
 * @version 1.0
 * @since 2017/4/17
 */
public abstract class QcloudHttpClient {

    protected final ClientConfig clientConfig;
    protected final CloseableHttpClient httpClient;

    protected final RequestConfig requestConfig;

    public QcloudHttpClient(ClientConfig config) {
        this.clientConfig = config;

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(config.getMaxConnectionCount());
        connectionManager.setDefaultMaxPerRoute(config.getMaxConnectionCount());
        this.httpClient = HttpClients.custom().setConnectionManager(connectionManager).build();

        this.requestConfig = RequestConfig.custom()
                .setConnectTimeout(config.getConnectionTimeout())
                .setConnectionRequestTimeout(config.getConnectionRequestTimeout())
                .setSocketTimeout(config.getSocketTimeout())
                .build();

        IdleConnectionEvictor idleConnectionEvictor = new IdleConnectionEvictor(connectionManager,
                config.getMonitorSleepTime(), TimeUnit.MILLISECONDS,
                config.getConnectionMaxIdleTime(), TimeUnit.MILLISECONDS);
        idleConnectionEvictor.start();
    }

    public ClientConfig getClientConfig() {
        return clientConfig;
    }

    protected abstract String sendPostRequest(HttpRequest httpRequest) throws QcloudSdkException;

    protected abstract String sendGetRequest(HttpRequest httpRequest) throws QcloudSdkException;

    public String sendHttpRequest(HttpRequest httpRequest) throws QcloudSdkException{
        HttpMethod method = httpRequest.getMethod();
        switch (method) {
            case GET:
                return sendGetRequest(httpRequest);
            case POST:
                return sendPostRequest(httpRequest);
            default:
                throw new ParamException("Unsupported http method");
        }
    }

    protected void setHeaders(HttpMessage message, Map<String, String> headers) {
        message.setHeader("Accept", "*/*");
        message.setHeader("Connection", "Keep-Alive");
        message.setHeader("User-Agent", this.clientConfig.getUserAgent());

        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                message.setHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    // 打印HTTP返回码非200的时候的错误信息
    protected String getErrorHttpResponseMsg(HttpRequest httpRequest, StatusLine responseStatus) {
        StringBuilder sb = new StringBuilder("HttpRequest:").append(httpRequest.toString());
        sb.append("\nHttpResponse:");
        if (responseStatus.getProtocolVersion() != null) {
            sb.append(" protocol:").append(responseStatus.getProtocolVersion().toString());
        }
        sb.append(", code:").append(responseStatus.getStatusCode());
        if (responseStatus.getReasonPhrase() != null) {
            sb.append(", reasonPhrase:").append(responseStatus.getReasonPhrase());
        }

        return sb.toString();
    }
}
