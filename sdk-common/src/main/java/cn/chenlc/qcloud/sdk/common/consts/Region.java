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

package cn.chenlc.qcloud.sdk.common.consts;

/**
 * 区域枚举
 *
 * @author chenlc
 * @version 1.0
 * @since 2017/4/22
 */
public enum Region {
    BEI_JING("bj"),
    GUANG_ZHOU("gz"),
    SHANG_HAI("sh"),
    XIANG_GANG("hk"),
    NORTH_AMERICA("ca"),
    SINGAPORE("sg"),
    SHANGHAI_FINANCE("shjr"),
    SHENZHEN_FINANCE("szjr"),
    GUANGZHOU_OPEN("gzopen");

    private final String value;

    Region(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}