package tech.sud.mgp.hello.common.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

public class ImageUtils {

    /**
     * 获取文件当中的bitmap，设置最大宽高，防止内存溢出
     */
    public static Bitmap getBitmap(File file, int maxWidth, int maxHeight) {
        if (file == null) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int maxWidth, int maxHeight) {
        return calculateInSampleSize(options.outWidth, options.outHeight, maxWidth, maxHeight);
    }

    private static int calculateInSampleSize(int width, int height, int maxWidth, int maxHeight) {
        int inSampleSize = 1;
        if (height > maxHeight || width > maxWidth) {
            int widthRatio = width / maxWidth;
            int heightRatio = height / maxHeight;
            if (widthRatio > heightRatio) {
                inSampleSize = widthRatio;
            } else {
                inSampleSize = heightRatio;
            }
        }
        return inSampleSize;
    }

}
