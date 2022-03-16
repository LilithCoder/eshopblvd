package com.hatsukoi.eshopblvd.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.http.HttpStatus;

import java.util.HashMap;

/**
 * 接口请求响应封装类
 * 描述：用来封装请求响应，功能包括快速构造500响应、200响应，响应内容的自定义
 *
 * @author gaoweilin
 */
public class CommonResponse extends HashMap<String, Object> {

    public static final String RESPONSE_SUCCESS_MSG = "success";
    public static final String RESPONSE_ERROR_MSG = "error";
    public static final String RESPONSE_DATA_KEY = "data";

    public CommonResponse(boolean isSuccess) {
        if (isSuccess) {
            put("code", HttpStatus.SC_OK);
            put("msg", RESPONSE_SUCCESS_MSG);
        } else {
            put("code", HttpStatus.SC_INTERNAL_SERVER_ERROR);
            put("msg", RESPONSE_ERROR_MSG);
        }
    }

    public static CommonResponse success() {
        return new CommonResponse(true);
    }

    public static CommonResponse success(String msg) {
        CommonResponse resp = new CommonResponse(true);
        resp.put("msg", msg);
        return resp;
    }

    public static CommonResponse error() {
        return new CommonResponse(false);
    }

    public static CommonResponse error(String msg) {
        CommonResponse resp = new CommonResponse(false);
        resp.put("msg", msg);
        return resp;
    }

    public static CommonResponse error(int code, String msg) {
        CommonResponse resp = new CommonResponse(false);
        resp.put("code", code);
        resp.put("msg", msg);
        return resp;
    }

    /**
     * 获取响应内容
     *
     * @param typeReference
     * @param <T>
     * @return T
     */
    public <T> T getData(TypeReference<T> typeReference) {
        Object data = get(RESPONSE_DATA_KEY);
        // Java 对象转换成 JSON 字符串
        String str = JSON.toJSONString(data);
        // JSON 字符串转换成 Java 对象，其中使用TypeReference可以明确的指定反序列化的类型
        T t = JSON.parseObject(str, typeReference);
        return t;
    }

    /**
     * 设置响应内容
     *
     * @param data
     */
    public CommonResponse setData(Object data) {
        put("data", data);
        return this;
    }
}
