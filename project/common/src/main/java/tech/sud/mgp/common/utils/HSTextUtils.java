package tech.sud.mgp.common.utils;

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

}
