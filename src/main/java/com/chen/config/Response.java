package com.chen.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

/**
 * 处理返回客户端的状态码，状态字，数据等.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response {

    private String code;
    private String msg;
    private Object result;

    public Response(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 操作成功！返回状态码，状态文字，结果对象。用以处理有返回数据的情况.
     *
     * @param result 待返回的结果值
     * @return Response
     */
    public static Response buildSuccess(Object result) {
        return new Response("00000", "Success", result);
    }


    public static Response buildFailure(Object result) {
        return new Response("11111", "Failure", result);
    }

    /**
     * 操作成功！返回状态码，状态文字。用以处理无返回数据的情况.
     *
     * @return Response
     */
    public static Response buildSuccess() {
        return new Response("00000", "Success", "操作成功");
    }

    /**
     * 操作成功！返回状态码，状态文字，结果对象数组。用以处理有返回数据的情况.
     *
     * @param result 待返回的结果值数组
     * @return Response
     */
    private static Response buildSuccess(ResponseEntity<Resource> result) {
        return new Response("00000", "Success", result);
    }

    /**
     * 操作成功！返回自定义状态码，自定义状态文字.
     *
     * @param code 自定义状态码
     * @param msg  自定义文字
     * @return Response
     */
    public static Response buildSuccess(String code, String msg) {
        return new Response(code, msg, null);
    }

    //这里是新增的

    /**
     * 操作失败！返回状态码，状态文字。用以处理无返回数据的情况.
     *
     * @return Response
     */
    public static Response buildFailure() {
        return new Response("11111", "Failure", "操作失败");
    }

    /**
     * 操作失败！返回自定义状态码，报错日志.
     *
     * @param code 自定义状态码
     * @param e    报错
     * @return Response
     */
    public static Response buildFailed(String code, RuntimeException e) {
        return new Response(code, e.getLocalizedMessage(), null);
    }

    /**
     * 操作失败！返回自定义状态码，自定义状态文字。用以处理无返回数据的情况.
     *
     * @param code 自定义状态码
     * @param msg  自定义文字提示
     * @return Response
     */
    public static Response buildFailed(String code, String msg) {
        return new Response(code, msg, null);
    }

}
