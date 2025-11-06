package tech.sud.mgp.hello;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

public class QuickStartUtils {

    private static SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy_MM_dd HH:mm:ss.SSS ", Locale.US);

    /**
     * 随机生成一个userId，用于演示
     * Generate a random userId for demonstration purposes.
     */
    public static String genUserID() {
        return md5Hex8(UUID.randomUUID().toString());
    }

    public static String md5Hex8(String plainText) {
        byte[] secretBytes;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(plainText.getBytes());
        } catch (NoSuchAlgorithmException e) {
            return plainText;
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = String.format("0%s", md5code);
        }

        return md5code.substring(8, 16);
    }

    public static String getDateTime() {
        Date d = new Date();
        String format = sDateFormat.format(d);
        // 时区偏移
        TimeZone timeZone = TimeZone.getDefault();
        String timeOffsetStr;
        if (timeZone == null) {
            timeOffsetStr = null;
        } else {
            int offsetInMillis = timeZone.getRawOffset();
            int hours = offsetInMillis / (1000 * 60 * 60);
            int minutes = Math.abs((offsetInMillis / (1000 * 60)) % 60);
            String sign = hours >= 0 ? "+" : "-";
            timeOffsetStr = "UTC" + sign + Math.abs(hours) + ":" + (minutes < 10 ? "0" : "") + minutes + " ";
        }
        return format + timeOffsetStr;
    }

}
