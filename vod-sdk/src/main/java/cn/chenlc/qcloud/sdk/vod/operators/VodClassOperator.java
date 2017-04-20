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

import cn.chenlc.qcloud.sdk.common.exceptions.ParamException;
import cn.chenlc.qcloud.sdk.common.exceptions.QcloudSdkException;
import cn.chenlc.qcloud.sdk.common.exceptions.ServerException;
import cn.chenlc.qcloud.sdk.common.http.HttpMethod;
import cn.chenlc.qcloud.sdk.common.http.HttpRequest;
import cn.chenlc.qcloud.sdk.common.http.QcloudHttpClient;
import cn.chenlc.qcloud.sdk.common.sign.Credential;
import cn.chenlc.qcloud.sdk.common.utils.DateUtils;
import cn.chenlc.qcloud.sdk.vod.IVodClassManager;
import cn.chenlc.qcloud.sdk.vod.ParamKeys;
import cn.chenlc.qcloud.sdk.vod.VodConstants;
import cn.chenlc.qcloud.sdk.vod.sign.Sign;
import cn.chenlc.qcloud.sdk.vod.vo.VodClassInfo;
import cn.chenlc.qcloud.sdk.vod.vo.VodClassSimpleInfo;
import cn.chenlc.qcloud.sdk.vod.vo.VodClassTreeMap;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 视频分类管理操作器
 *
 * @author chenlc
 * @version 1.0
 * @since 2017/4/17
 */
public class VodClassOperator extends AbstractOperator implements IVodClassManager {

    // 获取全部分类层级结构信息相关常量
    private static final class DESCRIBE_ALL_CLASS {
        private static final String ACTION = "DescribeAllClass";
        private static final String OUTPUT_INFO = "info";
        private static final String OUTPUT_SUB_CLASS = "subclass";
        private static final String OUTPUT_ID = "id";
        private static final String OUTPUT_PARENT_ID = "parent_id";
        private static final String OUTPUT_NAME = "name";
        private static final String OUTPUT_LEVEL = "level";
        private static final String OUTPUT_FILE_NUM = "file_num";
    }
    // 获取全局分类列表信息相关常量
    private static final class DESCRIBE_CLASS {
        private static final String ACTION = "DescribeClass";
        private static final String OUTPUT_ID = "id";
        private static final String OUTPUT_NAME = "name";
        private static final String OUTPUT_CREATE_TIME = "create_time";
        private static final String OUTPUT_UPDATE_TIME = "update_time";
    }
    // 创建视频分类相关常量
    private static final class CREATE_CLASS {
        private static final String ACTION = "CreateClass";
        private static final String INPUT_CLASS_NAME = "className";
        private static final String INPUT_PARENT_ID = "parentId";
        private static final String OUTPUT_NEW_CLASS_ID = "newClassId";
    }
    // 修改视频分类相关常量
    private static final class MODIFY_CLASS {
        private static final String ACTION = "ModifyClass";
        private static final String INPUT_CLASS_ID = "classId";
        private static final String INPUT_NEW_CLASS_NAME = "className";
    }
    // 删除视频分类相关常量
    private static final class DELETE_CLASS {
        private static final String ACTION = "DeleteClass";
        private static final String INPUT_CLASS_ID = "classId";
    }

    public VodClassOperator(Credential credential, QcloudHttpClient httpClient) {
        super(credential, httpClient);
    }

    @Override
    public int createClass(String className, Integer parentId) throws QcloudSdkException {
        if (StringUtils.isBlank(className)) {
            throw new IllegalArgumentException("className is empty");
        }
        Map<String, String> params = genCommonParams(CREATE_CLASS.ACTION, null);
        params.put(CREATE_CLASS.INPUT_CLASS_NAME, className);
        if (parentId != null) {
            params.put(CREATE_CLASS.INPUT_PARENT_ID, parentId.toString());
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
        return resJson.getIntValue(CREATE_CLASS.OUTPUT_NEW_CLASS_ID);
    }

    @Override
    public VodClassTreeMap describeAllClass() throws QcloudSdkException {
        Map<String, String> params = genCommonParams(DESCRIBE_ALL_CLASS.ACTION, null);
        params.put(ParamKeys.SIGNATURE_KEY, Sign.sign(credential, HttpMethod.GET, params));

        HttpRequest request = new HttpRequest();
        request.setUrl(VodConstants.REQUEST_URL).setMethod(HttpMethod.GET).setParams(params);

        String resJsonString = httpClient.sendHttpRequest(request);
        JSONObject resJson = JSON.parseObject(resJsonString);
        int code = resJson.getIntValue(ParamKeys.OUTPUT_CODE);
        if (code != 0) {
            throw new ServerException(code, resJson.getString(ParamKeys.OUTPUT_MESSAGE));
        }

        VodClassTreeMap tree = new VodClassTreeMap();
        JSONArray data = resJson.getJSONArray(ParamKeys.OUTPUT_DATA);

        for (int i = 0; i < data.size(); i++) {
            JSONObject nodeInfoJson = data.getJSONObject(i);
            VodClassTreeMap.TreeNode node = genTreeNode(nodeInfoJson);
            tree.addNode(node.getNodeInfo().getName(), node);
        }

        return tree;
    }

    private VodClassTreeMap.TreeNode genTreeNode(JSONObject jo) {
        JSONObject infoObj = jo.getJSONObject(DESCRIBE_ALL_CLASS.OUTPUT_INFO);
        VodClassInfo nodeInfo = new VodClassInfo();

        nodeInfo.setId(infoObj.getIntValue(DESCRIBE_ALL_CLASS.OUTPUT_ID));
        nodeInfo.setParentId(infoObj.getIntValue(DESCRIBE_ALL_CLASS.OUTPUT_PARENT_ID));
        nodeInfo.setName(infoObj.getString(DESCRIBE_ALL_CLASS.OUTPUT_NAME));
        nodeInfo.setLevel(infoObj.getIntValue(DESCRIBE_ALL_CLASS.OUTPUT_LEVEL));
        nodeInfo.setFileCount(infoObj.getIntValue(DESCRIBE_ALL_CLASS.OUTPUT_FILE_NUM));

        VodClassTreeMap.TreeNode result = new VodClassTreeMap.TreeNode(nodeInfo);

        JSONArray subClasses = jo.getJSONArray(DESCRIBE_ALL_CLASS.OUTPUT_SUB_CLASS);
        for (int i = 0; i < subClasses.size(); i++) {
            JSONObject sjo = subClasses.getJSONObject(i);
            VodClassTreeMap.TreeNode node = genTreeNode(sjo);
            result.addNode(node.getNodeInfo().getName(), node);
        }

        return result;
    }

    @Override
    public List<VodClassSimpleInfo> describeClass() throws QcloudSdkException {
        Map<String, String> params = genCommonParams(DESCRIBE_CLASS.ACTION, null);
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
        List<VodClassSimpleInfo> result = new ArrayList<>(data.size());
        for (int i = 0; i < data.size(); i++) {
            JSONObject jo = data.getJSONObject(i);
            VodClassSimpleInfo info = new VodClassSimpleInfo();
            info.setId(jo.getIntValue(DESCRIBE_CLASS.OUTPUT_ID));
            info.setName(jo.getString(DESCRIBE_CLASS.OUTPUT_NAME));
            info.setCreateTime(DateUtils.standardParse(jo.getString(DESCRIBE_CLASS.OUTPUT_CREATE_TIME)));
            info.setUpdateTime(DateUtils.standardParse(jo.getString(DESCRIBE_CLASS.OUTPUT_UPDATE_TIME)));
            result.add(info);
        }
        return result;
    }

    @Override
    public void modifyClass(Integer classId, String newClassName) throws QcloudSdkException {
        if (classId == null || StringUtils.isBlank(newClassName)) {
            throw new ParamException("classId or newClassName is empty!");
        }
        Map<String, String> params = genCommonParams(MODIFY_CLASS.ACTION, null);
        params.put(MODIFY_CLASS.INPUT_CLASS_ID, classId.toString());
        params.put(MODIFY_CLASS.INPUT_NEW_CLASS_NAME, newClassName);
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
    public void deleteClass(Integer classId) throws QcloudSdkException {
        if (classId == null) {
            throw new ParamException("classId is null!");
        }
        Map<String, String> params = genCommonParams(DELETE_CLASS.ACTION, null);
        params.put(DELETE_CLASS.INPUT_CLASS_ID, classId.toString());
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
}
