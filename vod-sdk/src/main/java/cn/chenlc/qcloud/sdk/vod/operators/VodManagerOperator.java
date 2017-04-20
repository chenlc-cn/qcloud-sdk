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

import cn.chenlc.qcloud.sdk.common.exceptions.QcloudSdkException;
import cn.chenlc.qcloud.sdk.common.exceptions.ServerException;
import cn.chenlc.qcloud.sdk.common.http.HttpMethod;
import cn.chenlc.qcloud.sdk.common.http.HttpRequest;
import cn.chenlc.qcloud.sdk.common.http.QcloudHttpClient;
import cn.chenlc.qcloud.sdk.common.sign.Credential;
import cn.chenlc.qcloud.sdk.vod.IVodManager;
import cn.chenlc.qcloud.sdk.vod.NamedParamPair;
import cn.chenlc.qcloud.sdk.vod.ParamKeys;
import cn.chenlc.qcloud.sdk.vod.VodConstants;
import cn.chenlc.qcloud.sdk.vod.sign.Sign;
import cn.chenlc.qcloud.sdk.vod.vo.VodFileFullInfo;
import cn.chenlc.qcloud.sdk.vod.vo.VodFileInfo;
import cn.chenlc.qcloud.sdk.vod.vo.VodFilePlayInfo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * 点播服务，视频管理操作实现
 *
 * @author chenlc
 * @version 1.0
 * @since 2017/4/18
 */
public class VodManagerOperator extends AbstractOperator implements IVodManager{

    private static final class DESCRIBE_VOD_PLAY_URLS {
        private static final String ACTION = "DescribeVodPlayUrls";
        private static final String INPUT_FILE_ID = "fileId";
        private static final String OUTPUT_PLAYSET = "playSet";
        private static final String OUTPUT_PLAYSET_URL = "url";
        private static final String OUTPUT_PLAYSET_DEFINITION = "definition";
        private static final String OUTPUT_PLAYSET_BITRATE = "vbitrate";
        private static final String OUTPUT_PLAYSET_HEIGHT = "vheight";
        private static final String OUTPUT_PLAYSET_WIDTH = "vwidth";
    }

    private static final class MODIFY_VOD_INFO {
        private static final String ACTION = "ModifyVodInfo";
        private static final String INPUT_FILE_ID = "fileId";
    }

    public VodManagerOperator(Credential credential, QcloudHttpClient httpClient) {
        super(credential, httpClient);
    }

    @Override
    public List<VodFilePlayInfo> describeVodPlayUrls(String fileId) throws QcloudSdkException {
        Map<String, String> params = genCommonParams(DESCRIBE_VOD_PLAY_URLS.ACTION, null);
        params.put(DESCRIBE_VOD_PLAY_URLS.INPUT_FILE_ID, fileId);
        params.put(ParamKeys.SIGNATURE_KEY, Sign.sign(credential, HttpMethod.GET, params));

        HttpRequest request = new HttpRequest();
        request.setUrl(VodConstants.REQUEST_URL).setMethod(HttpMethod.GET).setParams(params);

        String resJsonString = httpClient.sendHttpRequest(request);
        JSONObject resJson = JSON.parseObject(resJsonString);
        int code = resJson.getIntValue(ParamKeys.OUTPUT_CODE);
        if (code != 0) {
            throw new ServerException(code, resJson.getString(ParamKeys.OUTPUT_MESSAGE));
        }

        JSONArray playSet = resJson.getJSONArray(DESCRIBE_VOD_PLAY_URLS.OUTPUT_PLAYSET);
        List<VodFilePlayInfo> result = new ArrayList<>(playSet.size());
        for (int i = 0; i < playSet.size(); i++) {
            JSONObject playObj = playSet.getJSONObject(i);
            VodFilePlayInfo playInfo = new VodFilePlayInfo(
                    playObj.getString(DESCRIBE_VOD_PLAY_URLS.OUTPUT_PLAYSET_URL),
                    playObj.getIntValue(DESCRIBE_VOD_PLAY_URLS.OUTPUT_PLAYSET_DEFINITION),
                    playObj.getIntValue(DESCRIBE_VOD_PLAY_URLS.OUTPUT_PLAYSET_BITRATE),
                    playObj.getIntValue(DESCRIBE_VOD_PLAY_URLS.OUTPUT_PLAYSET_HEIGHT),
                    playObj.getIntValue(DESCRIBE_VOD_PLAY_URLS.OUTPUT_PLAYSET_WIDTH)
            );
            result.add(playInfo);
        }
        return result;
    }

    @Override
    public void getVideoInfo(String fileId, InfoTypes... infoFilter) throws QcloudSdkException {

    }

    @Override
    public List<VodFileFullInfo> describeVodInfo(NamedParamPair... queryParams) throws QcloudSdkException {
        return null;
    }

    @Override
    public List<VodFileInfo> describeVodPlayInfo(String fileName, Integer pageNo, Integer pageSize) throws QcloudSdkException {
        return null;
    }

    @Override
    public void createVodTags(String fileId, String... tags) throws QcloudSdkException {

    }

    @Override
    public void deleteVodTags(String fileId, String... tags) throws QcloudSdkException {

    }

    @Override
    public void modifyVodInfo(String fileId, NamedParamPair... modifyParams) throws QcloudSdkException {
        Map<String, String> params = genCommonParams(MODIFY_VOD_INFO.ACTION, null);
        params.put(MODIFY_VOD_INFO.INPUT_FILE_ID, fileId);
        for (NamedParamPair p : modifyParams) {
            String key = p.getKey();
            String value = p.getValue();
            System.out.println(key + " = " + value);
            params.put(p.getKey(), p.getValue());
        }
        params.put(ParamKeys.SIGNATURE_KEY, Sign.sign(credential, HttpMethod.GET, params));

        HttpRequest request = new HttpRequest();
        request.setUrl(VodConstants.REQUEST_URL).setMethod(HttpMethod.GET).setParams(params);

        String resJsonString = httpClient.sendHttpRequest(request);
        JSONObject resJson = JSON.parseObject(resJsonString);
        int code = resJson.getIntValue(ParamKeys.OUTPUT_CODE);
        if (code != 0) {
            throw new ServerException(code, resJson.getString(ParamKeys.OUTPUT_MESSAGE));
        }
    }

    @Override
    public void deleteVodFile(String fileId, int priority) throws QcloudSdkException {

    }

    @Override
    public void describeVodCover(String fileId, int type, String snapshotUrl) throws QcloudSdkException {

    }
}
