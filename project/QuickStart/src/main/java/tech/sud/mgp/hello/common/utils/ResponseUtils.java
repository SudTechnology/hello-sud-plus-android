package tech.sud.mgp.hello.common.utils;

import tech.sud.mgp.hello.common.http.param.BaseResponse;

/**
 * 网络请求返回工具类
 */
public class ResponseUtils {

    /**
     * 转换成展示的文案
     */
    public static <T> String conver(BaseResponse<T> response) {
        if (response == null) {
            return null;
        }
        return response.getRet_msg() + "(" + response.getRet_code() + ")";
    }

}
