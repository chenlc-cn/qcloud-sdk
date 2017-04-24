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

package cn.chenlc.qcloud.sdk.vod.operators;

import cn.chenlc.qcloud.sdk.common.consts.Region;
import cn.chenlc.qcloud.sdk.common.http.QcloudHttpClient;
import cn.chenlc.qcloud.sdk.common.sign.Credential;
import cn.chenlc.qcloud.sdk.vod.ParamKeys;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 抽象操作器， 提供一些公共方法
 *
 * @author chenlc
 * @version 1.0
 * @since 2017/4/17
 */
abstract class AbstractOperator {

    protected Credential credential;

    protected QcloudHttpClient httpClient;

    public AbstractOperator(Credential credential, QcloudHttpClient httpClient) {
        this.credential = credential;
        this.httpClient = httpClient;
    }

    protected Map<String, String> genCommonParams(String action, Region region) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put(ParamKeys.ACTION_KEY, action);
        if (region != null) {
            params.put(ParamKeys.REGION_KEY, region.getValue());
        }
        params.put(ParamKeys.TIMESTAMP_KEY, String.valueOf(System.currentTimeMillis() / 1000));
        params.put(ParamKeys.NONCE_KEY, integerNonce());
        params.put(ParamKeys.SECRET_ID_KEY, credential.getSecretId());
        return params;
    }

    protected String integerNonce() {
        return RandomStringUtils.randomNumeric(6, 10);
    }
}
