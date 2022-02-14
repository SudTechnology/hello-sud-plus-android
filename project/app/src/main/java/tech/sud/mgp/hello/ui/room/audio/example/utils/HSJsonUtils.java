package tech.sud.mgp.hello.ui.room.audio.example.utils;

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

}
