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
import cn.chenlc.qcloud.sdk.common.exceptions.ParamException;
import cn.chenlc.qcloud.sdk.common.exceptions.QcloudSdkException;
import cn.chenlc.qcloud.sdk.common.exceptions.ServerException;
import cn.chenlc.qcloud.sdk.common.http.HttpMethod;
import cn.chenlc.qcloud.sdk.common.http.HttpRequest;
import cn.chenlc.qcloud.sdk.common.http.QcloudHttpClient;
import cn.chenlc.qcloud.sdk.common.sign.Credential;
import cn.chenlc.qcloud.sdk.vod.IVodUpload;
import cn.chenlc.qcloud.sdk.vod.ParamKeys;
import cn.chenlc.qcloud.sdk.vod.VodConstants;
import cn.chenlc.qcloud.sdk.vod.sign.Sign;
import cn.chenlc.qcloud.sdk.vod.vo.UploadInitResponse;
import cn.chenlc.qcloud.sdk.vod.vo.UploadSuccessResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 视频文件上传操作实现
 *
 * @author chenlc
 * @version 1.0
 * @since 2017/4/20
 */
public class VodUploadOperator extends AbstractOperator implements IVodUpload {

    private static final Logger LOGGER = LoggerFactory.getLogger(VodUploadOperator.class);

    /** 文件上传通用字段名 */
    private static final class COMMON_KEYS {
        private static final String FILE_NAME = "fileName";
        private static final String FILE_SHA = "fileSha";
        private static final String FILE_SIZE = "fileSize";
        private static final String DATA_SIZE = "dataSize";
        private static final String FILE_TYPE = "fileType";
        private static final String FILE_ID = "fileId";
        private static final String CODE = "code";
        private static final String MESSAGE = "message";
        private static final String CODE_DESC = "codeDesc";
        private static final String CAN_RETRY = "canRetry";
        private static final String OFFSET = "offset";
        private static final String DATA_MD5 = "dataMd5";
        private static final String URL = "url";
        private static final String TAGS_PREFIX = "tags";
        private static final String CLASS_ID = "classId";
        private static final String IS_TRANSCODE = "isTranscode";
        private static final String IS_SCREENSHOT = "isScreenshot";
        private static final String IS_WATERMARK = "isWatermark";

        private static final String VALUE_TRUE = "1";
//        private static final String VALUE_FALSE = "0";
    }

    /** 上传初始化, 相关字段名 */
    private static final class INIT_UPLOAD {
        private static final String ACTION = "InitUpload";

        private static final String INPUT_STORE_TIME = "storeTime";

        private static final String OUTPUT_LIST_PARTS = "listParts";
        private static final String OUTPUT_DATA_LENGTH = "dataLen";
    }
    /** 分片上传，相关字段名 */
    private static final class UPLOAD_PART {
        private static final String ACTION = "UploadPart";
    }
    /** 结束分片上传相关字段名 */
    private static final class FINISH_UPLOAD {
        private static final String ACTION = "FinishUpload";
    }
    /** 小文件上传相关字段名 */
    private static final class SMALL_FILE_UPLOAD {
        private static final String ACTION = "SmallFileUpload";
        private static final String INPUT_EXTRA_USAGE = "extra.usage";
        private static final String INPUT_EXTRA_FILE_ID = "extra.fileId";
    }
    /** URL拉取视频上传 */
    private static final class MULTI_PULL_VOD_FILE {
        private static final String ACTION = "MultiPullVodFile";
        private static final String INPUT_PREFIX = "pullset";
        private static final String INPUT_FILE_MD5 = "fileMd5";
        private static final String INPUT_PRIORITY = "priority";
    }

    /** 默认分片大小 */
    private static final int DEFAULT_DATA_SIZE = 524288;
//    private static final int DEFAULT_DATA_SIZE = 1048576;

    private Region region;

    public VodUploadOperator(Credential credential, QcloudHttpClient httpClient) {
        super(credential, httpClient);
        this.region = httpClient.getClientConfig().getRegion();
    }

    @Override
    public UploadInitResponse initUpload(String fileName, String fileSha, long fileSize, long dataSize, String fileType) throws QcloudSdkException {
        return initUpload(fileName, fileSha, fileSize, dataSize, fileType, null);
    }

    @Override
    public UploadInitResponse initUpload(String fileName, String fileSha, long fileSize, long dataSize, String fileType, UploadOptionalParams ops) throws QcloudSdkException {
        Map<String, String> params = genCommonParams(INIT_UPLOAD.ACTION, region);
        params.put(COMMON_KEYS.FILE_NAME, fileName);
        params.put(COMMON_KEYS.FILE_SHA, fileSha);
        params.put(COMMON_KEYS.FILE_SIZE, String.valueOf(fileSize));
        params.put(COMMON_KEYS.DATA_SIZE, String.valueOf(dataSize));
        params.put(COMMON_KEYS.FILE_TYPE, fileType);

        // 处理可选参数
        if (ops != null) {
            if (ops.getClassId() != null) {
                params.put(COMMON_KEYS.CLASS_ID, ops.getClassId().toString());
            }
            if (Boolean.TRUE.equals(ops.getIsTranscode())) {
                params.put(COMMON_KEYS.IS_TRANSCODE, COMMON_KEYS.VALUE_TRUE);
            }
            if (Boolean.TRUE.equals(ops.getIsScreenshot())) {
                params.put(COMMON_KEYS.IS_SCREENSHOT, COMMON_KEYS.VALUE_TRUE);
            }
            if (Boolean.TRUE.equals(ops.getIsWatermark())) {
                params.put(COMMON_KEYS.IS_WATERMARK, COMMON_KEYS.VALUE_TRUE);
            }
            if (ops.getStoreTime() != null) {
                params.put(INIT_UPLOAD.INPUT_STORE_TIME, ops.getStoreTime().toString());
            }
            int tagIndex = 1;
            for (String tag : ops.getTags()) {
                params.put(COMMON_KEYS.TAGS_PREFIX + "." + tagIndex, tag);
                tagIndex++;
            }
        }

        // 签名
        params.put(ParamKeys.SIGNATURE_KEY, sign(HttpMethod.POST, params));

        HttpRequest request = new HttpRequest();
        request.setUrl(VodConstants.UPLOAD_REQUEST_URL)
                .setMethod(HttpMethod.POST)
                .setQueryParams(params);

        int retry = 0;
        int maxRetry = httpClient.getClientConfig().getMaxRetries();
        while (retry < maxRetry) {
            String resJsonString = httpClient.sendHttpRequest(request);
            JSONObject resJson = JSON.parseObject(resJsonString);
            UploadInitResponse response = new UploadInitResponse();

            int code = resJson.getInteger(COMMON_KEYS.CODE);
            String message = resJson.getString(COMMON_KEYS.MESSAGE);
            LOGGER.debug("第 [{}] 次初始化上传，返回：code = [{}], message = [{}]", retry + 1, code, message);
            if (code < 0) {
                int canRetry = resJson.getIntValue(COMMON_KEYS.CAN_RETRY);
                if (canRetry != 1 || ++retry > maxRetry) {
                    throw new ServerException(code, message);
                }
                continue;
            }
            response.setCode(code);
            response.setMessage(message);
            // 初始化完成
            if (code == 0) {
                return response;
            }
            // 断点续传
            if (code == 1) {
                response.setCodeDesc(resJson.getString(COMMON_KEYS.CODE_DESC));
                response.setDataSize(resJson.getLongValue(COMMON_KEYS.DATA_SIZE));
                JSONArray partList = resJson.getJSONArray(INIT_UPLOAD.OUTPUT_LIST_PARTS);
                if (partList != null) {
                    List<UploadInitResponse.PartInfo> parts = new ArrayList<>(partList.size());
                    for (int i = 0; i < partList.size(); i++) {
                        JSONObject part = partList.getJSONObject(i);
                        parts.add(new UploadInitResponse.PartInfo(
                                part.getLongValue(COMMON_KEYS.OFFSET),
                                part.getLongValue(INIT_UPLOAD.OUTPUT_DATA_LENGTH),
                                part.getString(COMMON_KEYS.DATA_MD5)
                        ));
                    }
                    response.setListParts(parts);
                }
                return response;
            }
            // 文件已存在
            if (code == 2) {
                response.setFileId(resJson.getString(COMMON_KEYS.FILE_ID));
                response.setUrl(resJson.getString(COMMON_KEYS.URL));
                return response;
            }
        }
        throw new ServerException("Unknown return code.");
    }

    @Override
    public void uploadPart(String fileSha, long offset, long dataSize, String dataMd5, byte[] data) throws QcloudSdkException {
        LOGGER.debug("分片上传，fileSha: [{}], offset: [{}] ...", fileSha, offset);
        Map<String, String> params = genCommonParams(UPLOAD_PART.ACTION, region);
        params.put(COMMON_KEYS.FILE_SHA, fileSha);
        params.put(COMMON_KEYS.OFFSET, String.valueOf(offset));
        params.put(COMMON_KEYS.DATA_SIZE, String.valueOf(dataSize));
        params.put(COMMON_KEYS.DATA_MD5, dataMd5);
        // 签名
        params.put(ParamKeys.SIGNATURE_KEY, sign(HttpMethod.POST, params));

        HttpRequest request = new HttpRequest();
        request.setUrl(VodConstants.UPLOAD_REQUEST_URL)
                .setMethod(HttpMethod.POST)
                .setQueryParams(params)
                .setBody(new ByteArrayEntity(data));

        int retry = 0;
        int maxRetries = httpClient.getClientConfig().getMaxRetries();
        while (retry < maxRetries) {
            String resString = httpClient.sendHttpRequest(request);
            JSONObject resJson = JSON.parseObject(resString);
            int code = resJson.getIntValue(COMMON_KEYS.CODE);
            String message = resJson.getString(COMMON_KEYS.MESSAGE);
            LOGGER.debug("第 [{}] 次上传，返回：code = [{}], message = [{}]", retry + 1, code, message);
            if (code < 0) {
                int canRetry = resJson.getIntValue(COMMON_KEYS.CAN_RETRY);
                if (canRetry != 1 || ++retry > maxRetries) {
                    throw new ServerException(code, message);
                }
                continue;
            }

            return;
        }

        throw new ServerException("上传失败");
    }

    @Override
    public UploadSuccessResponse finishUpload(String fileSha) throws QcloudSdkException {
        Map<String, String> params = genCommonParams(FINISH_UPLOAD.ACTION, region);
        params.put(COMMON_KEYS.FILE_SHA, fileSha);
        params.put(ParamKeys.SIGNATURE_KEY, sign(HttpMethod.POST, params));

        HttpRequest request = new HttpRequest();
        request.setUrl(VodConstants.UPLOAD_REQUEST_URL).setMethod(HttpMethod.POST).setQueryParams(params);

        int retry = 0;
        int maxRetries = httpClient.getClientConfig().getMaxRetries();
        while (retry < maxRetries) {
            String resString = httpClient.sendHttpRequest(request);
            JSONObject resJson = JSON.parseObject(resString);
            int code = resJson.getIntValue(COMMON_KEYS.CODE);
            String message = resJson.getString(COMMON_KEYS.MESSAGE);
            LOGGER.debug("第 [{}] 次结束上传，返回：code = [{}], message = [{}]", retry + 1, code, message);

            if (code < 0) {
                int canRetry = resJson.getIntValue(COMMON_KEYS.CAN_RETRY);
                if (canRetry != 1 || ++retry > maxRetries) {
                    throw new ServerException(code, message);
                }
                continue;
            }

            String fileId = resJson.getString(COMMON_KEYS.FILE_ID);
            String url = resJson.getString(COMMON_KEYS.URL);
            return new UploadSuccessResponse(fileId, url);
        }

        throw new ServerException("结束失败");
    }

    @Override
    public UploadSuccessResponse smallFileUpload(String fileName, String fileSha, long fileSize, String fileType, String vodFileId, byte[] data) throws QcloudSdkException {
        Map<String, String> params = genCommonParams(SMALL_FILE_UPLOAD.ACTION, region);
        params.put(COMMON_KEYS.FILE_NAME, fileName);
        params.put(COMMON_KEYS.FILE_SHA, fileSha);
        params.put(COMMON_KEYS.FILE_SIZE, String.valueOf(fileSize));
        params.put(COMMON_KEYS.DATA_SIZE, String.valueOf(fileSize));
        params.put(COMMON_KEYS.FILE_TYPE, fileType);
        if (vodFileId != null) {
            params.put(SMALL_FILE_UPLOAD.INPUT_EXTRA_USAGE, "1");
            params.put(SMALL_FILE_UPLOAD.INPUT_EXTRA_FILE_ID, vodFileId);
        }
        // 签名
        params.put(ParamKeys.SIGNATURE_KEY, sign(HttpMethod.POST, params));

        HttpRequest request = new HttpRequest();
        request.setUrl(VodConstants.UPLOAD_REQUEST_URL)
                .setMethod(HttpMethod.POST)
                .setBody(new ByteArrayEntity(data));

        int retry = 0;
        int maxRetries = httpClient.getClientConfig().getMaxRetries();
        while (retry < maxRetries) {
            String resString = httpClient.sendHttpRequest(request);
            JSONObject resJson = JSON.parseObject(resString);
            int code = resJson.getIntValue(COMMON_KEYS.CODE);
            String message = resJson.getString(COMMON_KEYS.MESSAGE);
            LOGGER.debug("第 [{}] 次上传，返回：code = [{}], message = [{}]", retry + 1, code, message);

            if (code < 0) {
                int canRetry = resJson.getIntValue(COMMON_KEYS.CAN_RETRY);
                if (canRetry != 1 || ++retry > maxRetries) {
                    throw new ServerException(code, message);
                }
                continue;
            }

            String fileId = resJson.getString(COMMON_KEYS.FILE_ID);
            String url = resJson.getString(COMMON_KEYS.URL);
            return new UploadSuccessResponse(fileId, url);
        }

        throw new ServerException("上传失败");
    }

    @Override
    public UploadSuccessResponse uploadVodFile(File file) throws IOException, QcloudSdkException {
        return uploadVodFile(file, null);
    }

    @Override
    public UploadSuccessResponse uploadVodFile(File file, UploadOptionalParams optionalParams) throws QcloudSdkException, IOException {
        if (file == null || !file.exists()) {
            throw new FileNotFoundException(file == null ? "" : file.getAbsolutePath());
        }
        if (!file.isFile()) {
            throw new ParamException("The file is not normal file.");
        }
        String fileName = file.getName();
        int suffixIndex = fileName.lastIndexOf('.') + 1;
        if (suffixIndex == 0) {
            throw new ParamException("无法识别文件类型，请确保文件名有后缀名。");
        }
        String suffix = fileName.substring(suffixIndex);
        return uploadVodFile(file, suffix, optionalParams);
    }

    @Override
    public UploadSuccessResponse uploadVodFile(File file, String fileType, UploadOptionalParams optionalParams) throws QcloudSdkException, IOException {
        if (file == null || !file.exists()) {
            throw new FileNotFoundException(file == null ? "" : file.getAbsolutePath());
        }
        if (!file.isFile()) {
            throw new ParamException("The file is not normal file.");
        }

        String fileName = file.getName().substring(0, file.getName().length() - fileType.length() - 1);
        String fileSha = calcSha1(file);
        long fileSize = file.length();

        // 上传初始化
        UploadInitResponse initResponse = initUpload(fileName, fileSha, fileSize, DEFAULT_DATA_SIZE, fileType, optionalParams);
        int returnCode = initResponse.getCode();
        if (returnCode < 0 || returnCode > 2) {
            LOGGER.debug("初始化文件上传失败：code = [{}], message = [{}]", returnCode, initResponse.getMessage());
            throw new ServerException(returnCode, initResponse.getMessage());
        }

        // 开始分片上传
        try (FileInputStream fin = new FileInputStream(file)) {

            if (returnCode == 0) {
                // 全新上传
                byte[] buffer = new byte[DEFAULT_DATA_SIZE];
                int offset = 0;
                //noinspection Duplicates
                while (offset < fileSize) {
                    int len = fin.read(buffer);
                    if (len < DEFAULT_DATA_SIZE) {
                        buffer = Arrays.copyOfRange(buffer, 0, len);
                    }
                    uploadPart(fileSha, offset, len, DigestUtils.md5Hex(buffer), buffer);
                    offset += len;
                }
            } else if (returnCode == 1) {
                // 断点续传
                int curDataSize = (int)initResponse.getDataSize();
                List<UploadInitResponse.PartInfo> uploadedParts = initResponse.getListParts();
                Collections.sort(uploadedParts, new Comparator<UploadInitResponse.PartInfo>() {
                    @Override
                    public int compare(UploadInitResponse.PartInfo o1, UploadInitResponse.PartInfo o2) {
                        return (int)(o1.getOffset() - o2.getOffset());
                    }
                });

                byte[] buffer = new byte[curDataSize];
                long offset = 0;
                for (UploadInitResponse.PartInfo part : uploadedParts) {
                    long partOffset = part.getOffset();
                    long partDataSize = part.getDataSize();
                    if (partDataSize != curDataSize && partOffset + partDataSize != fileSize) {
                        throw new ParamException("Data size of part is wrong!");
                    }
                    while (offset < partOffset) {
                        int len = fin.read(buffer);
                        uploadPart(fileSha, offset, len, DigestUtils.md5Hex(buffer), buffer);
                        offset += len;
                    }

                    // 分片已存在
                    offset += fin.skip(partDataSize);
                }

                // 上传剩余的连续分片
                //noinspection Duplicates
                while (offset < fileSize) {
                    int len = fin.read(buffer);
                    if (len < DEFAULT_DATA_SIZE) {
                        buffer = Arrays.copyOfRange(buffer, 0, len);
                    }
                    uploadPart(fileSha, offset, len, DigestUtils.md5Hex(buffer), buffer);
                    offset += len;
                }
            } else if (returnCode == 2) {
                LOGGER.info("文件已上传，fileId: [{}], fileUrl: [{}]", initResponse.getFileId(), initResponse.getUrl());
                return new UploadSuccessResponse(initResponse.getFileId(), initResponse.getUrl());
            }

        }

        return finishUpload(fileSha);
    }

    @Override
    public void multiPullVodFile(List<MultiPullParams> pullList) throws QcloudSdkException {
        if (pullList == null || pullList.size() == 0) {
            throw new ParamException("pullList is empty!");
        }
        Map<String, String> params = genCommonParams(MULTI_PULL_VOD_FILE.ACTION, region);
        for (int i = 0; i < pullList.size(); i++) {
            MultiPullParams pp = pullList.get(i);
            int index = i + 1;
            params.put(genMultiPullParam(COMMON_KEYS.URL, index), pp.getUrl());
            params.put(genMultiPullParam(COMMON_KEYS.FILE_NAME, index), pp.getFileName());
            if (pp.getFileMd5() != null) {
                params.put(genMultiPullParam(MULTI_PULL_VOD_FILE.INPUT_FILE_MD5, index), pp.getFileMd5());
            }
            if (Boolean.TRUE.equals(pp.getIsTranscode())) {
                params.put(genMultiPullParam(COMMON_KEYS.IS_TRANSCODE, index), COMMON_KEYS.VALUE_TRUE);
            }
            if (Boolean.TRUE.equals(pp.getIsScreenshot())) {
                params.put(genMultiPullParam(COMMON_KEYS.IS_SCREENSHOT, index), COMMON_KEYS.VALUE_TRUE);
            }
            if (Boolean.TRUE.equals(pp.getIsWaterMark())) {
                params.put(genMultiPullParam(COMMON_KEYS.IS_WATERMARK, index), COMMON_KEYS.VALUE_TRUE);
            }
            if (pp.getClassId() != null) {
                params.put(genMultiPullParam(COMMON_KEYS.CLASS_ID, index), pp.getClassId().toString());
            }
            if (pp.getTags().size() > 0) {
                StringBuilder tagBuilder = new StringBuilder();
                for (String tag : pp.getTags()) {
                    tagBuilder.append(tag).append(",");
                }
                tagBuilder.deleteCharAt(tagBuilder.length() - 1);
                params.put(genMultiPullParam(COMMON_KEYS.TAGS_PREFIX, index), tagBuilder.toString());
            }
            if (pp.getPriority() != null) {
                params.put(genMultiPullParam(MULTI_PULL_VOD_FILE.INPUT_PRIORITY, index), pp.getPriority().value());
            }
        }
        // 签名
        params.put(ParamKeys.SIGNATURE_KEY, Sign.sign(credential, HttpMethod.POST, params));

        HttpRequest request = new HttpRequest();
        request.setUrl(VodConstants.REQUEST_URL).setMethod(HttpMethod.POST);
        List<NameValuePair> ppList = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            ppList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        request.setBody(new UrlEncodedFormEntity(ppList, StandardCharsets.UTF_8));

        String resString = httpClient.sendHttpRequest(request);
        JSONObject resJson = JSON.parseObject(resString);
        int code = resJson.getIntValue(COMMON_KEYS.CODE);
        String message = resJson.getString(COMMON_KEYS.MESSAGE);
        LOGGER.debug("拉取上传结束，返回：code = [{}], message = [{}]", code, message);

        if (code < 0) {
            throw new ServerException(code, message);
        }
    }

    private String sign(HttpMethod method, Map<String, String> params) {
        //params.put(ParamKeys.SIGNATURE_METHOD_KEY, "HmacSHA256");
        return Sign.sign(credential, method, VodConstants.UPLOAD_REQUEST_HOST,
                VodConstants.UPLOAD_REQUEST_PATH, params);
    }

    private String calcSha1(File file) throws IOException {
        try (FileInputStream fin = new FileInputStream(file)) {
            return DigestUtils.sha1Hex(fin);
        }
    }

    private String genMultiPullParam(String name, int index) {
        return MULTI_PULL_VOD_FILE.INPUT_PREFIX + "." + index + "." + name;
    }
}
