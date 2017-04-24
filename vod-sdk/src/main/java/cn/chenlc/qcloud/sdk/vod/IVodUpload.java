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

import cn.chenlc.qcloud.sdk.common.exceptions.QcloudSdkException;
import cn.chenlc.qcloud.sdk.vod.vo.UploadSuccessResponse;
import cn.chenlc.qcloud.sdk.vod.vo.UploadInitResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 服务端视频上传操作接口
 *
 * @author chenlc
 * @version 1.0
 * @since 2017/4/20
 */
public interface IVodUpload {

    /**
     * 视频分片上传初始化
     *
     * @param fileName 视频文件本地名称，长度40个字节以内，不包含\/:*?"<>等特殊字符
     * @param fileSha 视频文件的SHA校验值
     * @param fileSize 视频文件的总大小，单位字节Byte
     * @param dataSize 视频分片大小，可选值：524288(512KB), 1048576(1MB)
     * @param fileType 视频文件的类型，一般为视频的后缀名称，如mp4,flv等
     * @throws QcloudSdkException 请求失败时抛出
     */
    UploadInitResponse initUpload(String fileName, String fileSha, long fileSize, long dataSize,
                                  String fileType)
            throws QcloudSdkException;

    /**
     * 视频分片上传初始化
     *
     * @param fileName 视频文件本地名称，长度40个字节以内，不包含\/:*?"<>等特殊字符
     * @param fileSha 视频文件的SHA校验值
     * @param fileSize 视频文件的总大小，单位字节Byte
     * @param dataSize 视频分片大小，可选值：524288(512KB), 1048576(1MB)
     * @param fileType 视频文件的类型，一般为视频的后缀名称，如mp4,flv等
     * @param optionalParams 可选参数集
     * @throws QcloudSdkException 请求失败时抛出
     */
    UploadInitResponse initUpload(String fileName, String fileSha, long fileSize, long dataSize,
                                  String fileType, UploadOptionalParams optionalParams)
            throws QcloudSdkException;

    /**
     * 视频分片上传
     *
     * @param fileSha 整个文件的SHA （<strong>注意，不是当前分片的SHA，必须与初始化上传的fileSha一致</strong>）
     * @param offset 分片在文件中的相对偏移，注意该值必须为dataSize的整数倍
     * @param dataSize 分片大小，除了最后一个分片，大小必须相同且保持与初始化时的设定一致
     * @param dataMd5 该分片所上传数据的MD5
     * @param data 视频分片数据
     * @throws QcloudSdkException 上传失败时抛出
     */
    void uploadPart(String fileSha, long offset, long dataSize, String dataMd5, byte[] data) throws QcloudSdkException;

    /**
     * 视频上传完成通知
     *
     * @param fileSha 整个文件的SHA值
     * @throws QcloudSdkException 请求失败时抛出
     */
    UploadSuccessResponse finishUpload(String fileSha) throws QcloudSdkException;

    /**
     * 视频封面上传
     *
     * @param fileName 文件本地名称
     * @param fileSha 文件SHA值
     * @param fileSize 文件大小
     * @param fileType 文件类型，例如mp4, flv等
     * @param vodFileId 上传封面对应的视频文件ID, 可选参数, 不填时表示小视频文件上传
     * @param data 文件数据
     * @throws QcloudSdkException 上传失败时抛出
     */
    UploadSuccessResponse smallFileUpload(String fileName, String fileSha, long fileSize, String fileType, String vodFileId, byte[] data) throws QcloudSdkException;

    /**
     * 分片上传视频文件，封装切分和分片上传逻辑， 文件类型通过后缀名获取
     *
     * @param file 要上传的文件
     * @throws QcloudSdkException 上传失败时抛出
     * @throws FileNotFoundException 找不到指定文件时抛出
     */
    UploadSuccessResponse uploadVodFile(File file) throws IOException, QcloudSdkException;

    /**
     * 分片上传视频文件，封装切分和分片上传逻辑， 文件类型通过后缀名获取
     *
     * @param file 要上传的文件
     * @param optionalParams 可选参数
     * @throws QcloudSdkException 上传失败时抛出
     * @throws FileNotFoundException 找不到指定文件时抛出
     */
    UploadSuccessResponse uploadVodFile(File file, UploadOptionalParams optionalParams) throws IOException, QcloudSdkException;

    /**
     * 分片上传视频文件，封装切分和分片上传逻辑
     *
     * @param file 要上传的文件
     * @param fileType 文件类型
     * @param optionalParams 可选参数
     * @throws QcloudSdkException 上传失败时抛出
     * @throws FileNotFoundException 找不到指定的文件
     */
    UploadSuccessResponse uploadVodFile(File file, String fileType, UploadOptionalParams optionalParams) throws IOException, QcloudSdkException;

    /**
     * 文件上传初始化，可选参数集
     */
    class UploadOptionalParams {
        private List<String> tags = new ArrayList<>();
        private Integer classId;
        private Boolean isTranscode;
        private Boolean isScreenshot;
        private Boolean isWatermark;

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        public void addTag(String tag) {
            this.tags.add(tag);
        }

        public Integer getClassId() {
            return classId;
        }

        public void setClassId(Integer classId) {
            this.classId = classId;
        }

        public Boolean getIsTranscode() {
            return isTranscode;
        }

        public void setIsTranscode(Boolean transcode) {
            isTranscode = transcode;
        }

        public Boolean getIsScreenshot() {
            return isScreenshot;
        }

        public void setIsScreenshot(Boolean screenshot) {
            isScreenshot = screenshot;
        }

        public Boolean getIsWatermark() {
            return isWatermark;
        }

        public void setIsWatermark(Boolean watermark) {
            isWatermark = watermark;
        }
    }
}
