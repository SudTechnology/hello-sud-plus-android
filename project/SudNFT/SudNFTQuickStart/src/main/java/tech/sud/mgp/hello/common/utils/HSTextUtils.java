package tech.sud.mgp.hello.common.utils;

import android.text.Editable;
import android.widget.EditText;

import java.util.regex.Pattern;

public class HSTextUtils {

    /**
     * 将输入的聊天内容字符串给过滤掉空字符
     */
    public static String filterEmptyInputChatContent(String content) {
        if (content == null || content.isEmpty()) return content;
        return replaceEmptyLineEnd(content);
    }

    public static String replaceEmptyLineEnd(String param) {
        if (param == null) return null;
        String str = param;
        str = str.replaceAll("\\n$", "");
        str = str.replaceAll("\\r$", "");
        return str;
    }

    /**
     * 判断是否为整数
     *
     * @param str 传入的字符串
     * @return 是整数返回true, 否则返回false
     */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static String getText(EditText editText) {
        Editable text = editText.getText();
        if (text != null) {
            return text.toString();
        }
        return null;
    }

}
