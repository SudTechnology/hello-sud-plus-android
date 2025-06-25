package tech.sud.mgp.hello.common.utils;

import android.util.Base64;

public class Base64Utils {

    /**
     * 将base64的字符串，转成byte数组
     *
     * @param base64Str
     * @return
     */
    public static byte[] base64Decode(String base64Str) {
        if (base64Str == null || base64Str.length() == 0)
            return null;
        return Base64.decode(base64Str, Base64.NO_WRAP);
    }

    /**
     * 将byte数组转成可见的base64字符串
     *
     * @param buf
     * @return
     */
    public static String base64Encode(byte[] buf) {
        if (buf == null || buf.length == 0)
            return null;
        return Base64.encodeToString(buf, Base64.NO_WRAP);
    }

}
