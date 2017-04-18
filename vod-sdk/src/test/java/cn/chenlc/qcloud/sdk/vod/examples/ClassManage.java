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

package cn.chenlc.qcloud.sdk.vod.examples;

import cn.chenlc.qcloud.sdk.vod.VodClient;
import cn.chenlc.qcloud.sdk.vod.vo.VodClassInfo;
import cn.chenlc.qcloud.sdk.vod.vo.VodClassTree;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @author chenlc
 * @version 1.0
 * @since 2017/4/17
 */
public class ClassManage {

    public static void main(String[] args) throws Exception {

        int appId = 1252719796;
        String secretId = "AKID2zkNq9TNDqMk3uFQRIzVwFLFzXs1ZXYN";
        String secretKey = "mPtBGLF1b0FI9QZXJvMbeV0Tluq041nU";

        VodClient vodClient = new VodClient(appId, secretId, secretKey);

        VodClassTree tree = vodClient.describeAllClass();

        System.out.println(tree.toString());

        System.out.print(JSON.toJSONString(vodClient.describeClass()));
    }
}
