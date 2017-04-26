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

package cn.chenlc.qcloud.sdk.vod;

/**
 * 腾讯云点播系统常量表
 *
 * @author chenlc
 * @version 1.0
 * @since 2017/4/18
 */
public class VodConstants {

    /** 点播服务接口，默认请求域名 */
    public static final String REQUEST_HOST = "vod.api.qcloud.com";
    /** 点播服务接口，默认请求路径 */
    public static final String REQUEST_PATH = "/v2/index.php";
    /** 点播服务接口，默认请求URL */
    public static final String REQUEST_URL = "https://vod.api.qcloud.com/v2/index.php";

    /** 点播服务，视频上传接口请求的域名 */
    public static final String UPLOAD_REQUEST_HOST = "vod2.qcloud.com";
    /** 点播服务，视频上传接口请求的路径 */
    public static final String UPLOAD_REQUEST_PATH = "/v3/index.php";
    /** 点播服务，视频上传接口请求的URL */
    public static final String UPLOAD_REQUEST_URL = "https://vod2.qcloud.com/v3/index.php";
}
