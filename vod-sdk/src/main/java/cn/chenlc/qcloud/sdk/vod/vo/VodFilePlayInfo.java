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
 * 视频文件播放信息
 *
 * @author chenlc
 * @version 1.0
 * @since 2017/4/18
 */
public class VodFilePlayInfo {

    private String url;

    private int definition;

    private int bitrate;

    private int height;

    private int width;

    public VodFilePlayInfo(String url, int definition, int bitrate, int height, int width) {
        this.url = url;
        this.definition = definition;
        this.bitrate = bitrate;
        this.height = height;
        this.width = width;
    }

    public String getUrl() {
        return url;
    }

    public int getDefinition() {
        return definition;
    }

    public int getBitrate() {
        return bitrate;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
