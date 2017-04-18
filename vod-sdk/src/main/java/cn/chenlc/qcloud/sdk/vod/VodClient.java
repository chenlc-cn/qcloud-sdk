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

import cn.chenlc.qcloud.sdk.common.exceptions.ParamException;
import cn.chenlc.qcloud.sdk.common.exceptions.QcloudSdkException;
import cn.chenlc.qcloud.sdk.common.exceptions.ServerException;
import cn.chenlc.qcloud.sdk.common.http.*;
import cn.chenlc.qcloud.sdk.common.sign.Credential;
import cn.chenlc.qcloud.sdk.vod.operators.VodClassOperator;
import cn.chenlc.qcloud.sdk.vod.sign.Sign;
import cn.chenlc.qcloud.sdk.vod.vo.VodClassSimpleInfo;
import cn.chenlc.qcloud.sdk.vod.vo.VodClassTree;
import cn.chenlc.qcloud.sdk.vod.vo.VodFileInfo;
import cn.chenlc.qcloud.sdk.vod.vo.VodFilePlayInfo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 点播服务客户端
 *
 * @author chenlc
 * @version 1.0
 * @since 2017/4/15
 */
public class VodClient implements IVodClassManager, IVod1_0Compatibility{

    private final Credential credential;
    private final QcloudHttpClient httpClient;

    private IVodClassManager classOperator;

    public VodClient(int appId, String secretId, String secretKey) {
        this(new ClientConfig(), new Credential(appId, secretId, secretKey));
    }

    public VodClient(ClientConfig clientConfig, Credential credential) {
        this(credential, new DefaultQcloudHttpClient(clientConfig));
    }

    public VodClient(Credential credential, QcloudHttpClient httpClient) {
        this.credential = credential;
        this.httpClient = httpClient;
        this.classOperator = new VodClassOperator(credential, httpClient);
    }

    @Override
    public String createClass(String className, Integer parentId) throws QcloudSdkException {
        return this.classOperator.createClass(className, parentId);
    }

    @Override
    public VodClassTree describeAllClass() throws QcloudSdkException {
        return this.classOperator.describeAllClass();
    }

    @Override
    public List<VodClassSimpleInfo> describeClass() throws QcloudSdkException {
        return this.classOperator.describeClass();
    }

    @Override
    public void modifyClass(Integer classId, String newClassName) throws QcloudSdkException {
        this.classOperator.modifyClass(classId, newClassName);
    }

    @Override
    public void deleteClass(Integer classId) throws QcloudSdkException {
        this.classOperator.deleteClass(classId);
    }


    private static final class DESCRIBE_RECORD_PLAY_INFO {
        private static final String ACTION = "DescribeRecordPlayInfo";
        private static final String INPUT_VID = "vid";
        private static final String OUTPUT_FILE_ID = "fileId";
        private static final String OUTPUT_FILE_NAME = "fileName";
        private static final String OUTPUT_DURATION = "duration";
        private static final String OUTPUT_STATUS = "status";
        private static final String OUTPUT_IMAGE_URL = "image_url";
        private static final String OUTPUT_PLAYSET = "playSet";
        private static final String OUTPUT_PLAYSET_URL = "url";
        private static final String OUTPUT_PLAYSET_DEFINITION = "definition";
        private static final String OUTPUT_PLAYSET_BITRATE = "vbitrate";
        private static final String OUTPUT_PLAYSET_HEIGHT = "vheight";
        private static final String OUTPUT_PLAYSET_WIDTH = "vwidth";
    }
    /**
     * 点播1.0兼容接口， 腾讯云直播、互动直播录制文件会进入点播系统，每个录制文件会有唯一的video_id(简称vid).
     * 该接口用于依照vid获取视频信息
     *
     * @param vid 视频唯一ID
     * @return 视频文件信息集合
     * @throws QcloudSdkException 请求失败时
     */
    @Override
    public List<VodFileInfo> describeRecordPlayInfo(String vid) throws QcloudSdkException {
        if (StringUtils.isBlank(vid)) {
            throw new ParamException("vid is empty");
        }
        Map<String, String> params = new LinkedHashMap<>();
        params.put(ParamKeys.ACTION_KEY, DESCRIBE_RECORD_PLAY_INFO.ACTION);
        params.put(ParamKeys.TIMESTAMP_KEY, String.valueOf(System.currentTimeMillis() / 1000));
        params.put(ParamKeys.NONCE_KEY, RandomStringUtils.randomAlphanumeric(8));
        params.put(ParamKeys.SECRET_ID_KEY, credential.getSecretId());
        params.put(DESCRIBE_RECORD_PLAY_INFO.INPUT_VID, vid);
        params.put(ParamKeys.SIGNATURE_KEY, Sign.sign(credential, HttpMethod.GET, params));

        HttpRequest request = new HttpRequest();
        request.setUrl(VodConstants.REQUEST_URL).setMethod(HttpMethod.GET).setParams(params);

        String resJsonString = httpClient.sendHttpRequest(request);
        JSONObject resJson = JSON.parseObject(resJsonString);
        int code = resJson.getIntValue(ParamKeys.OUTPUT_CODE);
        if (code != 0) {
            throw new ServerException(code, resJson.getString(ParamKeys.OUTPUT_MESSAGE));
        }

        JSONArray data = resJson.getJSONArray(ParamKeys.OUTPUT_DATA);
        List<VodFileInfo> result = new ArrayList<>(data.size());
        for (int i = 0; i < data.size(); i++) {
            JSONObject fileObj = data.getJSONObject(i);
            VodFileInfo fileInfo = new VodFileInfo(
                    fileObj.getString(DESCRIBE_RECORD_PLAY_INFO.OUTPUT_FILE_ID),
                    fileObj.getString(DESCRIBE_RECORD_PLAY_INFO.OUTPUT_FILE_NAME),
                    fileObj.getIntValue(DESCRIBE_RECORD_PLAY_INFO.OUTPUT_DURATION),
                    fileObj.getIntValue(DESCRIBE_RECORD_PLAY_INFO.OUTPUT_STATUS),
                    fileObj.getString(DESCRIBE_RECORD_PLAY_INFO.OUTPUT_IMAGE_URL)
            );
            JSONArray playSet = fileObj.getJSONArray(DESCRIBE_RECORD_PLAY_INFO.OUTPUT_PLAYSET);
            for (int j = 0; j < playSet.size(); j++) {
                JSONObject playSetObj = playSet.getJSONObject(j);
                VodFilePlayInfo playInfo = new VodFilePlayInfo(
                        playSetObj.getString(DESCRIBE_RECORD_PLAY_INFO.OUTPUT_PLAYSET_URL),
                        playSetObj.getIntValue(DESCRIBE_RECORD_PLAY_INFO.OUTPUT_PLAYSET_DEFINITION),
                        playSetObj.getIntValue(DESCRIBE_RECORD_PLAY_INFO.OUTPUT_PLAYSET_BITRATE),
                        playSetObj.getIntValue(DESCRIBE_RECORD_PLAY_INFO.OUTPUT_PLAYSET_HEIGHT),
                        playSetObj.getIntValue(DESCRIBE_RECORD_PLAY_INFO.OUTPUT_PLAYSET_WIDTH)
                );
                fileInfo.addPlayInfo(playInfo);
            }
            result.add(fileInfo);
        }
        return result;
    }

}
