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

package cn.chenlc.qcloud.sdk.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期时间处理工具类
 *
 * @author chenlc
 * @version 1.0
 * @since 2017/4/17
 */
public class DateUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(DateUtils.class);

    private static final ThreadLocal<DateFormat> STANDARD_DATETIME_FORMATTER = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    /**
     * 标准格式的日期时间格式化，输出格式为：yyyy-MM-dd HH:mm:ss
     *
     * @param date 日期时间
     * @return "yyyy-MM-dd HH:mm:ss"格式的日期时间字符串
     */
    public static String standardFormat(Date date) {
        return STANDARD_DATETIME_FORMATTER.get().format(date);
    }

    /**
     * 标准格式的日期时间字符串解析， 输入参数需满足"yyyy-MM-dd HH:mm:ss"格式
     *
     * @param dateString 日期时间字符串，需满足"yyyy-MM-dd HH:mm:ss"格式
     * @return 解析得到的日期时间
     * @throws IllegalArgumentException 输入字符串不满足格式要求时抛出
     */
    public static Date standardParse(String dateString) {
        try {
            return STANDARD_DATETIME_FORMATTER.get().parse(dateString);
        } catch (ParseException e) {
            String message = "Unsupported date format: {}" + dateString;
            LOGGER.error(message);
            throw new IllegalArgumentException(message);
        }
    }
}
