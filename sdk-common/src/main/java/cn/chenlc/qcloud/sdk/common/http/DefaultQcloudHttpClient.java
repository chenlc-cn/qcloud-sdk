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

import cn.chenlc.qcloud.sdk.common.exceptions.NetworkException;
import cn.chenlc.qcloud.sdk.common.exceptions.ParamException;
import cn.chenlc.qcloud.sdk.common.exceptions.QcloudSdkException;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * 默认的HTTP Client实现
 *
 * @author chenlc
 * @version 1.0
 * @since 2017/4/17
 */
public class DefaultQcloudHttpClient extends QcloudHttpClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultQcloudHttpClient.class);

    public DefaultQcloudHttpClient(ClientConfig config) {
        super(config);
    }

    @Override
    protected String sendPostRequest(HttpRequest httpRequest) throws QcloudSdkException {
        String url = httpRequest.getUrl();
        return null;
    }

    @Override
    protected String sendGetRequest(HttpRequest request) throws QcloudSdkException {
        LOGGER.debug("Execute GET request ...");
        String url = request.getUrl();
        HttpGet httpGet;
        String responseString = "";
        int retry = 0;
        int maxRetryCount = this.clientConfig.getMaxRetries();

        while (retry < maxRetryCount) {
            try {
                URIBuilder uriBuilder = new URIBuilder(url);
                Map<String, String> params = request.getParams();
                for (String key : params.keySet()) {
                    uriBuilder.addParameter(key, params.get(key));
                }
                URI urlWithParams = uriBuilder.build();
                LOGGER.debug("GET {}", urlWithParams.toString());
                httpGet = new HttpGet(urlWithParams);
            } catch (URISyntaxException e) {
                throw new ParamException("Invalid url: " + url);
            }

            httpGet.setConfig(this.requestConfig);
            setHeaders(httpGet, request.getHeaders());

            try {
                HttpResponse response = httpClient.execute(httpGet);
                StatusLine statusLine = response.getStatusLine();
                LOGGER.debug("Server response: {} {} {}", statusLine.getProtocolVersion(), statusLine.getStatusCode(), statusLine.getReasonPhrase());
                int statusCode = statusLine.getStatusCode();
                if (statusCode == 200 || statusCode == 400 || (statusCode > 200 && statusCode < 300)) {
                    responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
                    LOGGER.debug("Response Body: {}", responseString);
                    return responseString;
                } else {
                    String errMsg = getErrorHttpResponseMsg(request, statusLine);
                    throw new ParamException(errMsg);
                }
            } catch (IOException e) {
                retry++;
                if (retry == maxRetryCount) {
                    throw new NetworkException("HttpRequeest: " + request + "\nException: " + e);
                }
            } finally {
                httpGet.releaseConnection();
            }
        }
        return responseString;
    }
}
