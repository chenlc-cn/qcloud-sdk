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
 * 视频文件详细信息
 *
 * @author chenlc
 * @version 1.0
 * @since 2017/4/18
 */
public class VodFileFullInfo {



    public static final class BasicInfo {
        private final String name;
        private final int size;
        private final int duration;
        private final String description;
        private final String status;
        private final long createTime;
        private final long updateTime;
        private final long expireTime;
        private final int classId;
        private final String className;
        private final int playerId;
        private final String coverUrl;
        private final String type;
        private final String sourceVideoUrl;

        public BasicInfo(String name, int size, int duration, String description, String status, long createTime,
                         long updateTime, long expireTime, int classId, String className, int playerId,
                         String coverUrl, String type, String sourceVideoUrl) {
            this.name = name;
            this.size = size;
            this.duration = duration;
            this.description = description;
            this.status = status;
            this.createTime = createTime;
            this.updateTime = updateTime;
            this.expireTime = expireTime;
            this.classId = classId;
            this.className = className;
            this.playerId = playerId;
            this.coverUrl = coverUrl;
            this.type = type;
            this.sourceVideoUrl = sourceVideoUrl;
        }

        public String getName() {
            return name;
        }

        public int getSize() {
            return size;
        }

        public int getDuration() {
            return duration;
        }

        public String getDescription() {
            return description;
        }

        public String getStatus() {
            return status;
        }

        public long getCreateTime() {
            return createTime;
        }

        public long getUpdateTime() {
            return updateTime;
        }

        public long getExpireTime() {
            return expireTime;
        }

        public int getClassId() {
            return classId;
        }

        public String getClassName() {
            return className;
        }

        public int getPlayerId() {
            return playerId;
        }

        public String getCoverUrl() {
            return coverUrl;
        }

        public String getType() {
            return type;
        }

        public String getSourceVideoUrl() {
            return sourceVideoUrl;
        }
    }

    public static final class TranscodeInfo {

    }
}
