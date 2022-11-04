package tech.sud.mgp.hello.ui.common.utils;

import android.content.Context;

import java.io.File;

public class FilePath {

    private static final String DIR_NAME = "sud";
    private static final String ROCKET_THUMB_FILE_NAME = "rocket_thumb.png";

    /** 获取火箭缩略图文件路径 */
    public static File getRocketThumbFilePath(Context context) {
        if (context == null) {
            return null;
        }
        File dir = getExternalFilesDir(context);
        return new File(dir, ROCKET_THUMB_FILE_NAME);
    }

    private static File getExternalFilesDir(Context context) {
        return context.getExternalFilesDir(DIR_NAME);
    }

}
