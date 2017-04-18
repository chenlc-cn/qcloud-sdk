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

package cn.chenlc.qcloud.sdk.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * JSON结果
 *
 * @author chenlc
 * @version 1.0
 * @since 2017/4/17
 */
public class JsonResult {

    public static final int SUCCESS = 0;
    public static final String SUCCESS_MESSAGE = "SUCCESS";
    public static final int FAILED = -1;
    public static final String FAILED_MESSAGE = "FAILED";

    private final int code;

    private final String message;

    private final Object data;

    public JsonResult(Integer code, String message, Object data) {
        if (code == null || message == null) {
            throw new IllegalArgumentException("code and message can not be null!");
        }
        this.code = code;
        this.message = message;
        this.data = data == null ? new JSONObject() : data;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    @SuppressWarnings("unchecked")
    public <T> T getData(Class<T> clazz) {
        return (T) this.data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JsonResult that = (JsonResult) o;

        return code == that.code && message.equals(that.message) && data.equals(that.data);
    }

    @Override
    public int hashCode() {
        int result = code;
        result = 31 * result + message.hashCode();
        result = 31 * result + data.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public static JsonResult success() {
        return success(null);
    }

    public static JsonResult success(Object data) {
        return new JsonResult(SUCCESS, SUCCESS_MESSAGE, data);
    }

    public static JsonResult failed() {
        return failed(FAILED, FAILED_MESSAGE);
    }

    public static JsonResult failed(int code, String message) {
        return new JsonResult(code, message, null);
    }
}
