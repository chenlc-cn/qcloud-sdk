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

import org.apache.http.HttpEntity;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * HTTP请求封装
 *
 * @author chenlc
 * @version 1.0
 * @since 2017/4/17
 */
public class HttpRequest {

    private String url = "";
    private HttpMethod method = HttpMethod.POST;
    private HttpContentType contentType = HttpContentType.APPLICATION_JSON;

    private Map<String, String> headers = new LinkedHashMap<>();
    private Map<String, String> queryParams = new LinkedHashMap<>();

    private HttpEntity body;
//    private byte[] body;

    public String getUrl() {
        return url;
    }

    public HttpRequest setUrl(String url) {
        this.url = url;
        return this;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public HttpRequest setMethod(HttpMethod method) {
        this.method = method;
        return this;
    }

    public HttpContentType getContentType() {
        return contentType;
    }

    public HttpRequest setContentType(HttpContentType contentType) {
        this.contentType = contentType;
        return this;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public HttpRequest setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public HttpRequest addHeader(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public HttpRequest setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
        return this;
    }

    public HttpRequest addQueryParam(String key, String value) {
        this.queryParams.put(key, value);
        return this;
    }

    public HttpEntity getBody() {
        return body;
    }

    public void setBody(HttpEntity body) {
        this.body = body;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("url:").append(url).append(", method:").append(method).append(", ConentType:")
                .append(contentType.toString()).append("\n");

        sb.append("Headers:\n");
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append("key:").append(entry.getKey());
            sb.append(", value:").append(entry.getValue());
            sb.append("\n");
        }

        sb.append("queryParams:\n");
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            sb.append("key:").append(entry.getKey());
            sb.append(", value:").append(entry.getValue());
            sb.append("\n");
        }

        return sb.toString();
    }
}
