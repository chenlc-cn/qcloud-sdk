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

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.IdleConnectionEvictor;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * HTTP请求工具
 *
 * @author chenlc
 * @version 1.0
 * @since 2017/4/17
 */
public class HttpRequestUtil {

    private static HttpRequestUtil instance = new HttpRequestUtil();

    private static final int MAX_TOTAL = 100;
    private static final int DEFAULT_MAX_PER_ROUTE = 100;

    private CloseableHttpClient httpClient;
    private PoolingHttpClientConnectionManager connectionManager;
    //private IdleConnectionEvictor

    private HttpRequestUtil() {
        this.connectionManager = new PoolingHttpClientConnectionManager();
        this.connectionManager.setMaxTotal(MAX_TOTAL);
        this.connectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUTE);
        this.httpClient = HttpClients.custom()
                .setConnectionManager(this.connectionManager)
                .build();


    }

    public static String getJson() {

        return "";
    }
}
