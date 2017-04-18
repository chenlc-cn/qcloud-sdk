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
import cn.chenlc.qcloud.sdk.vod.vo.VodFileInfo;

import java.util.List;

/**
 * 点播1.0版本兼容接口， 用于处理直播、互动直播录制的文件
 *
 * @author chenlc
 * @version 1.0
 * @since 2017/4/18
 */
public interface IVod1_0Compatibility {

    /**
     * 点播1.0兼容接口， 腾讯云直播、互动直播录制文件会进入点播系统，每个录制文件会有唯一的video_id(简称vid).
     * 该接口用于依照vid获取视频信息
     *
     * @param vid 视频唯一ID
     * @return 视频文件信息集合
     * @throws QcloudSdkException 请求失败时
     */
    List<VodFileInfo> describeRecordPlayInfo(String vid) throws QcloudSdkException;
}
