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

package cn.chenlc.qcloud.sdk.vod.vo;

/**
 * 结束上传接口返回信息
 *
 * @author chenlc
 * @version 1.0
 * @since 2017/4/21
 */
public class UploadSuccessResponse {

    /** 文件ID */
    private final String fileId;
    /** 文件URL */
    private final String url;

    public UploadSuccessResponse(String fileId, String url) {
        this.fileId = fileId;
        this.url = url;
    }

    /**
     * 获取文件ID
     *
     * @return 文件ID
     */
    public String getFileId() {
        return fileId;
    }

    /**
     * 获取文件URL
     * @return 文件URL
     */
    public String getUrl() {
        return url;
    }
}
