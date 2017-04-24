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
import org.apache.http.entity.ByteArrayEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
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

        private static final String VALUE_TRUE = "1";
        //private static final String VALUE_FALSE = "0";
    }

    /** 上传初始化, 相关字段名 */
    private static final class INIT_UPLOAD {
        private static final String ACTION = "InitUpload";
        private static final String INPUT_TAGS_PREFIX = "tags.";
        private static final String INPUT_CLASS_ID = "classId";
        private static final String INPUT_IS_TRANSCODE = "isTranscode";
        private static final String INPUT_IS_SCREENSHOT = "isScreenshot";
        private static final String INPUT_IS_WATERMARK = "isWatermark";

        private static final String OUTPUT_LIST_PARTS = "listParts";
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

    /** 默认分片大小 */
    private static final int DEFAULT_DATA_SIZE = 1048576;

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
    public UploadInitResponse initUpload(String fileName, String fileSha, long fileSize, long dataSize, String fileType, UploadOptionalParams optionalParams) throws QcloudSdkException {
        Map<String, String> params = genCommonParams(INIT_UPLOAD.ACTION, region);
        params.put(COMMON_KEYS.FILE_NAME, fileName);
        params.put(COMMON_KEYS.FILE_SHA, fileSha);
        params.put(COMMON_KEYS.FILE_SIZE, String.valueOf(fileSize));
        params.put(COMMON_KEYS.DATA_SIZE, String.valueOf(dataSize));
        params.put(COMMON_KEYS.FILE_TYPE, fileType);
        // 处理可选参数
        if (optionalParams != null) {
            if (optionalParams.getClassId() != null) {
                params.put(INIT_UPLOAD.INPUT_CLASS_ID, optionalParams.getClassId().toString());
            }
            if (Boolean.TRUE.equals(optionalParams.getIsTranscode())) {
                params.put(INIT_UPLOAD.INPUT_IS_TRANSCODE, COMMON_KEYS.VALUE_TRUE);
            }
            if (Boolean.TRUE.equals(optionalParams.getIsScreenshot())) {
                params.put(INIT_UPLOAD.INPUT_IS_SCREENSHOT, COMMON_KEYS.VALUE_TRUE);
            }
            if (Boolean.TRUE.equals(optionalParams.getIsWatermark())) {
                params.put(INIT_UPLOAD.INPUT_IS_WATERMARK, COMMON_KEYS.VALUE_TRUE);
            }

            int i = 1;
            for (String tag : optionalParams.getTags()) {
                params.put(INIT_UPLOAD.INPUT_TAGS_PREFIX + i, tag);
                i++;
            }
        }
        // 签名
        params.put(ParamKeys.SIGNATURE_KEY, sign(HttpMethod.GET, params));

        HttpRequest request = new HttpRequest();
        request.setUrl(VodConstants.UPLOAD_REQUEST_URL)
                .setMethod(HttpMethod.GET)
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
                                part.getLongValue(COMMON_KEYS.DATA_SIZE),
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
        params.put(ParamKeys.SIGNATURE_KEY, fileSha);

        HttpRequest request = new HttpRequest();
        request.setUrl(VodConstants.UPLOAD_REQUEST_URL).setMethod(HttpMethod.GET).setQueryParams(params);

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

        FileInputStream fin = new FileInputStream(file);
        FileChannel finChannel = fin.getChannel();
        // 准备上传初始化参数
        String fileName = file.getName();
        fileName = fileName.substring(0, fileName.length() - fileType.length() - 1);
        String fileSha = DigestUtils.sha1Hex(fin);
        long fileSize = finChannel.size();

        UploadInitResponse initResponse = initUpload(fileName, fileSha, fileSize, DEFAULT_DATA_SIZE, fileType, optionalParams);
        int returnCode = initResponse.getCode();
        if (returnCode == 0) {
            // 全新上传
            ByteBuffer buffer = ByteBuffer.allocate(DEFAULT_DATA_SIZE);
            long offset = 0;
            // noinspection Duplicates
            while (offset < fileSize) {
                buffer.clear();
                finChannel.read(buffer);
                byte[] partData = buffer.array();
                String partDataMd5 = DigestUtils.md5Hex(partData);
                uploadPart(fileSha, offset, DEFAULT_DATA_SIZE, partDataMd5, partData);
                offset += partData.length;
            }
        } else if (returnCode == 1) {
            // 断点续传
            List<UploadInitResponse.PartInfo> existsPartList = initResponse.getListParts();
            Collections.sort(existsPartList, new Comparator<UploadInitResponse.PartInfo>() {
                @Override
                public int compare(UploadInitResponse.PartInfo o1, UploadInitResponse.PartInfo o2) {
                    return (int)(o1.getOffset() - o2.getOffset());
                }
            });

            ByteBuffer buffer = ByteBuffer.allocate(DEFAULT_DATA_SIZE);
            int offset = 0;
            // 遍历已存在的数据块
            for (UploadInitResponse.PartInfo part : existsPartList) {
                if (part.getDataSize() != DEFAULT_DATA_SIZE) {
                    throw new ParamException("Data size of part is different with default data size.");
                }
                buffer.clear();
                finChannel.read(buffer);
                if (part.getOffset() == offset) {
                    offset += part.getDataSize();
                    continue;
                }

                while (offset < part.getOffset()) {
                    byte[] partData = buffer.array();
                    String partDataMd5 = DigestUtils.md5Hex(partData);
                    uploadPart(fileSha, offset, DEFAULT_DATA_SIZE, partDataMd5, partData);
                    offset += partData.length;
                }

                offset += part.getDataSize();
            }
            // 上传剩余的数据块
            //noinspection Duplicates
            while (offset < fileSize) {
                buffer.clear();
                finChannel.read(buffer);
                byte[] partData = buffer.array();
                String partDataMd5 = DigestUtils.md5Hex(partData);
                uploadPart(fileSha, offset, DEFAULT_DATA_SIZE, partDataMd5, partData);
                offset += partData.length;
            }
        } else if (returnCode == 2) {
            LOGGER.info("文件已上传，fileId: [{}], fileUrl: [{}]", initResponse.getFileId(), initResponse.getUrl());
            return new UploadSuccessResponse(initResponse.getFileId(), initResponse.getUrl());
        } else {
            throw new ServerException(returnCode, initResponse.getMessage());
        }

        return finishUpload(fileSha);
    }

    private String sign(HttpMethod method, Map<String, String> params) {
        params.put(ParamKeys.SIGNATURE_METHOD_KEY, "HmacSHA256");
        return Sign.sign(credential, method, VodConstants.UPLOAD_REQUEST_HOST,
                VodConstants.UPLOAD_REQUEST_PATH, params);
    }
}
