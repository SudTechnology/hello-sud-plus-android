package tech.sud.mgp.hello.ui.common.utils;

import android.content.Context;

import java.io.File;

public class FilePath {

    private static final String DIR_NAME = "HelloSud";
    private static final String ROCKET_THUMB_FILE_NAME = "rocket_thumb";

    /** 获取火箭缩略图文件目录 */
    public static File getRocketThumbFileDir(Context context) {
        if (context == null) {
            return null;
        }
        File dir = getSudExternalFilesDir(context);
        return new File(dir, ROCKET_THUMB_FILE_NAME);
    }

    /** 获取App在sd卡里面的内置存储文件路径 */
    private static File getSudExternalFilesDir(Context context) {
        return context.getExternalFilesDir(DIR_NAME);
    }

}
