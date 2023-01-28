package com.brookezb.bhs.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API响应结果包装类
 *
 * @author brooke_zb
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class R<T> {
    /* 是否成功 */
    private boolean success;
    /* 承载数据 */
    private T data;
    /* 返回消息 */
    private String msg;

    /**
     * 操作成功，返回消息
     */
    public static R<Void> ok() {
        return okWithMsg("请求成功");
    }

    /**
     * 操作成功，返回消息
     *
     * @param msg 返回消息
     */
    public static R<Void> okWithMsg(String msg) {
        return ok(null, msg);
    }

    /**
     * 操作成功，返回数据
     *
     * @param data 数据
     */
    public static <T> R<T> ok(T data) {
        return ok(data, "请求成功");
    }

    /**
     * 操作成功，返回数据
     *
     * @param data 数据
     * @param msg  返回消息
     */
    public static <T> R<T> ok(T data, String msg) {
        return new R<>(true, data, msg);
    }

    /**
     * 操作失败，返回默认消息
     */
    public static <T> R<T> fail() {
        return fail("请求失败");
    }

    /**
     * 操作失败，返回消息
     *
     * @param msg 返回消息
     */
    public static <T> R<T> fail(String msg) {
        return new R<>(false, null, msg);
    }
}
