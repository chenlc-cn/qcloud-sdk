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
import cn.chenlc.qcloud.sdk.vod.vo.VodFileFullInfo;
import cn.chenlc.qcloud.sdk.vod.vo.VodFileInfo;
import cn.chenlc.qcloud.sdk.vod.vo.VodFilePlayInfo;

import java.util.List;

/**
 * 腾讯云点播系统， 视频管理操作接口
 *
 * @author chenlc
 * @version 1.0
 * @since 2017/4/18
 */
public interface IVodManager {

    /**
     * 视频的多种信息类型
     */
    enum InfoTypes {
        /** 基础信息 */
        BASIC_INFO("basicInfo"),
        /** 转码结果信息 */
        TRANSCODE_INFO("transcodeInfo"),
        /** 雪碧图信息 */
        IMAGE_SPRITE_INFO("imageSpriteInfo"),
        /** 指定时间点的截图信息 */
        SNAPSHOT_BY_TIME_OFFSET_INFO("snapshotByTimeOffsetInfo");

        private String value;

        InfoTypes(String value) {
            this.value = value;
        }

        public String value() {
            return this.value;
        }
    }

    class ModifyVodInfoParamBuilder {
        public static NamedParamPair classId(String value) {
            return new NamedParamPair("classId", value);
        }
        public static NamedParamPair fileName(String value) {
            return new NamedParamPair("fileName", value);
        }
        public static NamedParamPair fileIntro(String value) {
            return new NamedParamPair("fileIntro", value);
        }
        public static NamedParamPair expireTime(String value) {
            return new NamedParamPair("expireTime", value);
        }
    }

    /**
     * 获取指定视频的播放信息，包括播放地址、格式、码率、高度、宽度信息
     *
     * <p>
     *     <strong>注意： 如果已经切换到点播4.0， 建议使用</strong>
     * </p>
     *
     * @param fileId 待获取信息的视频的ID
     * @return 指定视频的播放信息列表
     * @throws QcloudSdkException 请求出错时抛出
     */
    List<VodFilePlayInfo> describeVodPlayUrls(String fileId) throws QcloudSdkException;

    /**
     * 获取单个视频的多种信息，包括：
     * <ul>
     *     <li>基础信息(basicInfo): 包括视频名称、大小、时长、封面图片等</li>
     *     <li>转码结果信息(transcodeInfo): 包括该视频转码生成的各种码率的视频的地址、规格、码率、分辨率等</li>
     *     <li>雪碧图信息(imageSprinteInfo): 对视频截取雪碧图之后，雪碧图的相关信息</li>
     *     <li>指定时间点截图信息(snapshotByTimeOffsetInfo): 对视频依照指定时间点截图后，各个截图的信息。</li>
     * </ul>
     * <p><strong>注意：可以指定只返回部分信息</strong></p>
     *
     * @param fileId 待获取信息的视频的ID
     * @param infoFilter 指定需要返回的信息，可同时指定多个信息。如果未指定，默认返回所有信息。
     *
     *                   备选项： {@link InfoTypes#BASIC_INFO BASIC_INFO}, {@link InfoTypes#TRANSCODE_INFO TRANSCODE_INFO},
     *                   {@link InfoTypes#IMAGE_SPRITE_INFO IMAGE_SPRITE_INFO},
     *                   {@link InfoTypes#SNAPSHOT_BY_TIME_OFFSET_INFO SNAPSHOT_BY_TIME_OFFSET_INFO}
     * @throws QcloudSdkException 请求出错时抛出
     */
    void getVideoInfo(String fileId, InfoTypes... infoFilter) throws QcloudSdkException;

    /**
     * 批量获取视频属性信息， 包括名称、介绍、大小、时长、状态、唯一码(vid)、创建时间、修改时间、分类ID、分类名称、封面图、标签列表、描述等
     *
     * @param queryParams 查询参数列表
     * @return 与参数列表相关的视频文件列表
     * @throws QcloudSdkException 请求失败时抛出
     * @see NamedParamPair
     */
    List<VodFileFullInfo> describeVodInfo(NamedParamPair... queryParams) throws QcloudSdkException;

    /**
     * 根据视频名称前缀搜索视频，并返回其播放信息
     *
     * @param fileName 文件名前缀
     * @param pageNo 分页页号
     * @param pageSize 分页大小
     * @return 携带播放信息的视频文件列表
     * @throws QcloudSdkException 请求失败时抛出
     */
    List<VodFileInfo> describeVodPlayInfo(String fileName, Integer pageNo, Integer pageSize) throws QcloudSdkException;

    /**
     * 为视频增加标签
     *
     * @param fileId 操作的视频文件ID
     * @param tags 添加的标签列表
     * @throws QcloudSdkException 请求失败时抛出
     */
    void createVodTags(String fileId, String... tags) throws QcloudSdkException;

    /**
     * 删除视频的标签，支持为单个视频删除多个标签
     *
     * @param fileId 操作的视频文件ID
     * @param tags 要删除的标签
     * @throws QcloudSdkException 请求失败时抛出
     */
    void deleteVodTags(String fileId, String... tags) throws QcloudSdkException;

    /**
     * 修改视频文件信息，包括文件名、描述、分类、过期时间等。
     *
     * <p><strong>注意：修改视频文件的过期时间，仅点播4.0支持</strong></p>
     *
     * @param fileId 待修改的文件ID
     * @param modifyParams 要修改的信息列表
     * @throws QcloudSdkException 请求失败时抛出
     * @see NamedParamPair
     */
    void modifyVodInfo(String fileId, NamedParamPair... modifyParams) throws QcloudSdkException;

    /**
     * 删除视频文件， 视频被删除后， 其所有附属对象（转码结果、雪碧图等）也将被删除
     *
     * @param fileId 要删除的视频文件ID
     * @param priority 优先级，默认0， 0：中； 1：高； 2：低
     * @throws QcloudSdkException 请求失败时抛出
     */
    void deleteVodFile(String fileId, int priority) throws QcloudSdkException;

    /**
     * 为视频设置显示封面， 仅支持设置url. 如需上传本地图片，需先调用上传接口上传。
     *
     * @param fileId 操作的视频文件ID
     * @param type 封面设置方法，当前只支持 1 ，表示使用截图地址
     * @param snapshotUrl 截图URL地址
     * @throws QcloudSdkException 请求失败时抛出
     * @see //TODO 链接上传接口
     */
    void describeVodCover(String fileId, int type, String snapshotUrl) throws QcloudSdkException;
}
