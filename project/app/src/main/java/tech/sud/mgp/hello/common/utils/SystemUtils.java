package tech.sud.mgp.hello.common.utils;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.DeviceUtils;

import java.util.Locale;

import tech.sud.mgp.hello.BuildConfig;

/**
 * 系统相关工具类
 */
public class SystemUtils {

    /**
     * 获取渠道标记
     */
    public static String getChannel() {
        return BuildConfig.CHANNEL;
    }

    /**
     * 获取系统版本号
     */
    public static String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取设备品牌
     */
    public static String getDeviceBrand() {
        return Build.BRAND;
    }

    /**
     * 获取设备id
     */
    public static String getDeviceId() {
        return DeviceUtils.getUniqueDeviceId();
    }

    /**
     * 获取语言码
     *
     * @param context
     * @return
     */
    public static String getLanguageCode(Context context) {
        return localeToLanguageCode(getLocale(context));
    }

    /**
     * 获取locale
     */
    public static Locale getLocale(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return context.getResources().getConfiguration().getLocales().get(0);
            }
            return context.getResources().getConfiguration().locale;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * locale对象转换语言码
     */
    public static String localeToLanguageCode(Locale locale) {
        if (locale == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        String language = locale.getLanguage();
        if (language.length() > 0) {
            sb.append(language);
        }
        String country = locale.getCountry();
        if (TextUtils.isEmpty(country)) {
            switch (language) {
                case "zh": // 中文
                    sb.append("-");
                    sb.append("CN");
                    break;
                case "en": // 英语
                    sb.append("-");
                    sb.append("US");
                    break;
                case "ar": // 阿拉伯语
                    sb.append("-");
                    sb.append("AE");
                    break;
                case "in": //印尼语
                    sb.append("-");
                    sb.append("ID");
                    break;
                case "ms": // 马来
                    sb.append("-");
                    sb.append("MY");
                    break;
                case "th": // 泰国
                    sb.append("-");
                    sb.append("TH");
                    break;
                case "vi": // 越南
                    sb.append("-");
                    sb.append("VN");
                    break;
                case "ko": //韩国
                    sb.append("-");
                    sb.append("KR");
                    break;
                case "es": // 西班牙
                    sb.append("-");
                    sb.append("ES");
                    break;
                case "ja": //日本
                    sb.append("-");
                    sb.append("JP");
                    break;
            }
        } else {
            sb.append("-");
            sb.append(country);
        }
        return sb.toString();
    }

    /**
     * 获取{versionName.versionCode}
     */
    public static String getAppVersion() {
        return AppUtils.getAppVersionName() + "." + AppUtils.getAppVersionCode();
    }

    public static String getVersionName() {
        return AppUtils.getAppVersionName();
    }

    public static int getVersionCode() {
        return AppUtils.getAppVersionCode();
    }

}
