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

package cn.chenlc.qcloud.sdk.vod.sign;

import cn.chenlc.qcloud.sdk.common.http.HttpMethod;
import cn.chenlc.qcloud.sdk.common.sign.Credential;
import cn.chenlc.qcloud.sdk.vod.ParamKeys;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.HmacUtils;

import java.util.Arrays;
import java.util.Map;

/**
 * 点播服务器，API请求参数签名工具
 *
 * @author chenlc
 * @version 1.0
 * @since 2017/4/15
 */
public class Sign {

    private static final String HMAC_SHA256 = "HmacSHA256";

    /**
     * 点播服务签名， 当参数列表中，SignatureMethod参数指定为"HmacSHA256"时， 采用HMAC_SHA256算法签名
     *
     * @param credential 安全访问凭证
     * @param method HTTP请求方法， POST或GET
     * @param requestHost 请求的主机
     * @param requestPath 请求的路径
     * @param params 参数列表
     * @return 签名字符串
     */
    public static String sign(Credential credential, HttpMethod method,
                              String requestHost, String requestPath,
                              Map<String, String> params) {
        String forSign = generateStringForSign(credential, method, requestHost, requestPath, params);
        byte[] signedBytes;
        if (HMAC_SHA256.equals(params.get(ParamKeys.SIGNATURE_METHOD_KEY))) {
            signedBytes = HmacUtils.hmacSha256(credential.getSecretKey(), forSign);
        } else {
            signedBytes = HmacUtils.hmacSha1(credential.getSecretKey(), forSign);
        }
        return Base64.encodeBase64String(signedBytes);
    }

    /*
     * 封装参数处理
     */
    private static String generateStringForSign(Credential credential, HttpMethod method,
                                                String requestHost, String requestPath,
                                                Map<String, String> params) {
        if (credential == null || params == null) {
            throw new IllegalArgumentException();
        }
        String[] keyNames = params.keySet().toArray(new String[0]);
        Arrays.sort(keyNames);

        StringBuilder paramsForSign = new StringBuilder();
        for (int i=0; i<keyNames.length; i++) {
            if (i != 0) {
                paramsForSign.append("&");
            }
            String key = keyNames[i];
            String value = params.get(key);
            paramsForSign.append(key).append("=").append(value.replace('_', '.'));
        }

        return method.name() +
                requestHost +
                requestPath + "?" +
                paramsForSign;
    }
}
