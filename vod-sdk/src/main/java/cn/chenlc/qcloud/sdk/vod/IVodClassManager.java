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
import cn.chenlc.qcloud.sdk.vod.vo.VodClassSimpleInfo;
import cn.chenlc.qcloud.sdk.vod.vo.VodClassTreeMap;

import java.util.List;

/**
 * 腾讯云点播服务，视频分类管理操作接口
 *
 * @author chenlc
 * @version 1.0
 * @since 2017/4/18
 */
public interface IVodClassManager {

    /**
     * 创建分类
     *
     * @param className 分类名称
     * @param parentId 父分类ID， 可选， 不填时为一级分类
     * @return 新创建的分类的ID
     * @throws QcloudSdkException 请求失败时抛出
     */
    int createClass(String className, Integer parentId) throws QcloudSdkException;

    /**
     * 获取全部分类信息
     *
     * @return 全部分类信息的树形结构
     * @throws QcloudSdkException 获取出错时抛出
     */
    VodClassTreeMap describeAllClass() throws QcloudSdkException;

    /**
     * 获取全局分类列表
     *
     * @return 全局分类列表
     * @throws QcloudSdkException 请求失败时抛出
     */
    List<VodClassSimpleInfo> describeClass() throws QcloudSdkException;

    /**
     * 修改视频分类属性，包括名称
     *
     * @param classId 待修改的分类ID
     * @param newClassName 新的分类名称，允许中英文、数字和圆括号
     * @throws QcloudSdkException 请求失败时抛出
     */
    void modifyClass(Integer classId, String newClassName) throws QcloudSdkException;

    /**
     * 删除视频分类
     *
     * @param classId 待删除的分类ID
     * @throws QcloudSdkException 请求失败时抛出
     */
    void deleteClass(Integer classId) throws QcloudSdkException;
}
