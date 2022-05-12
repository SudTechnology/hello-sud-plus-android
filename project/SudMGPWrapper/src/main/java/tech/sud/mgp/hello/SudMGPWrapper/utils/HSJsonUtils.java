package tech.sud.mgp.hello.SudMGPWrapper.utils;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.GsonUtils;

/**
 * json解析工具
 */
public class HSJsonUtils {

    /**
     * 解析json
     *
     * @return 如果解析出错，则返回空对象
     */
    public static <T> T fromJson(final String json, @NonNull final Class<T> type) {
        try {
            return GsonUtils.fromJson(json, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 对象解析成json
     *
     * @param object
     * @return
     */
    public static String toJson(final Object object) {
        return GsonUtils.toJson(object);
    }

}
