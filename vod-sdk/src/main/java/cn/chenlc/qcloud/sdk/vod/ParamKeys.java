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
 * 参数名称枚举
 *
 * @author chenlc
 * @version 1.0
 * @since 2017/4/17
 */
public class ParamKeys {

    // 公共请求参数
    public static final String ACTION_KEY = "Action";
    public static final String REGION_KEY = "Region";
    public static final String TIMESTAMP_KEY = "Timestamp";
    public static final String NONCE_KEY = "Nonce";
    public static final String SECRET_ID_KEY = "SecretId";
    public static final String SIGNATURE_KEY = "Signature";
    public static final String SIGNATURE_METHOD_KEY = "SignatureMethod";

    // 公共响应参数
    public static final String OUTPUT_CODE = "code";
    public static final String OUTPUT_MESSAGE = "message";
    public static final String OUTPUT_DATA = "data";

}
