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

import java.util.List;

/**
 * 视频文件，初始化上传接口返回信息
 *
 * @author chenlc
 * @version 1.0
 * @since 2017/4/20
 */
public class UploadInitResponse {

    private int code;
    private String message;
    private String codeDesc;
    private List<PartInfo> listParts;
    private long dataSize;
    private String fileId;
    private String url;
    private boolean canRetry;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCodeDesc() {
        return codeDesc;
    }

    public void setCodeDesc(String codeDesc) {
        this.codeDesc = codeDesc;
    }

    public List<PartInfo> getListParts() {
        return listParts;
    }

    public void setListParts(List<PartInfo> listParts) {
        this.listParts = listParts;
    }

    public long getDataSize() {
        return dataSize;
    }

    public void setDataSize(long dataSize) {
        this.dataSize = dataSize;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isCanRetry() {
        return canRetry;
    }

    public void setCanRetry(boolean canRetry) {
        this.canRetry = canRetry;
    }

    public static class PartInfo {
        private final long offset;
        private final long dataSize;
        private final String dataMd5;

        public PartInfo(long offset, long dataSize, String dataMd5) {
            this.offset = offset;
            this.dataSize = dataSize;
            this.dataMd5 = dataMd5;
        }

        public long getOffset() {
            return offset;
        }

        public long getDataSize() {
            return dataSize;
        }

        public String getDataMd5() {
            return dataMd5;
        }
    }
}
