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

import cn.chenlc.qcloud.sdk.common.consts.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HTTP客户端配置
 *
 * @author chenlc
 * @version 1.0
 * @since 2017/4/17
 */
public class ClientConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientConfig.class);

    /* 默认的最大维持HTTP连接数 */
    private static final int DEFAULT_MAX_CONNECTION_COUNT = 100;
    /* 默认的连接超时时间，单位毫秒 */
    private static final int DEFAULT_CONNECTION_TIMEOUT = 30 * 1000;
    /* 默认的获取连接请求等待时间(所有连接被占用时，会等待释放，这里是等待的最大超时时间，-1表示不超时 */
    private static final int DEFAULT_CONNECTION_REQUEST_TIMEOUT = -1;
    /* 默认的SOCKET读取超时时间，单位毫秒 */
    private static final int DEFAULT_SOCKET_TIMEOUT = 30 * 1000;
    /* 默认的HTTP连接监控线程巡检间隔时间 */
    private static final int DEFAULT_MONITOR_SLEEP_TIME = 60 * 1000;
    /* 默认的HTTP连接最大空闲时间 */
    private static final int DEFAULT_MAX_IDLE_TIME = 10 * 60 * 1000;
    /* 默认的user_agent标识 */
    private static final String DEFAULT_USER_AGENT = "cn.chenlc.qcloud.sdk v1.0.0";
    /* 发生网络异常时，默认的重试次数 */
    private static final int DEFAULT_MAX_RETRIES = 3;


    private int maxConnectionCount = DEFAULT_MAX_CONNECTION_COUNT;
    private int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
    private int connectionRequestTimeout = DEFAULT_CONNECTION_REQUEST_TIMEOUT;
    private int socketTimeout = DEFAULT_SOCKET_TIMEOUT;
    private int monitorSleepTime = DEFAULT_MONITOR_SLEEP_TIME;
    private int connectionMaxIdleTime = DEFAULT_MAX_IDLE_TIME;
    private int maxRetries = DEFAULT_MAX_RETRIES;
    private String userAgent = DEFAULT_USER_AGENT;
    private Region region;

    public int getMaxConnectionCount() {
        return maxConnectionCount;
    }

    public ClientConfig setMaxConnectionCount(int maxConnectionCount) {
        this.maxConnectionCount = maxConnectionCount;
        return this;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public ClientConfig setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public int getConnectionRequestTimeout() {
        return connectionRequestTimeout;
    }

    public ClientConfig setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
        return this;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public ClientConfig setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
        return this;
    }

    public int getMonitorSleepTime() {
        return monitorSleepTime;
    }

    public ClientConfig setMonitorSleepTime(int monitorSleepTime) {
        this.monitorSleepTime = monitorSleepTime;
        return this;
    }

    public int getConnectionMaxIdleTime() {
        return connectionMaxIdleTime;
    }

    public ClientConfig setConnectionMaxIdleTime(int connectionMaxIdleTime) {
        this.connectionMaxIdleTime = connectionMaxIdleTime;
        return this;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public ClientConfig setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
        return this;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public ClientConfig setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }
}
