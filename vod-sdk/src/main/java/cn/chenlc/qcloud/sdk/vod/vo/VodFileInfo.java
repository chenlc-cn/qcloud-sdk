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

import java.util.ArrayList;
import java.util.List;

/**
 * 视频文件信息描述对象
 *
 * @author chenlc
 * @version 1.0
 * @since 2017/4/18
 */
public class VodFileInfo {

    private String fileId;

    private String fileName;

    private int duration;

    private int status;

    private String imageUrl;

    private List<VodFilePlayInfo> playSet;

    public VodFileInfo(String fileId, String fileName, int duration, int status, String imageUrl) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.duration = duration;
        this.status = status;
        this.imageUrl = imageUrl;
        this.playSet = new ArrayList<>();
    }

    public String getFileId() {
        return fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public int getDuration() {
        return duration;
    }

    public int getStatus() {
        return status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<VodFilePlayInfo> getPlaySet() {
        return playSet;
    }

    public void addPlayInfo(VodFilePlayInfo playInfo) {
        this.playSet.add(playInfo);
    }
}
