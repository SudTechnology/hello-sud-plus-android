package tech.sud.mgp.hello.ui.room.common.gift.utils;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class FileUtils {
    private static final String SEPARATOR = File.separator;//路径分隔符

    /**
     * 复制res/raw中的文件到指定目录
     *
     * @param context     上下文
     * @param id          资源ID
     * @param fileName    文件名
     * @param storagePath 目标文件夹的路径
     */
    public static void copyFilesFromRaw(Context context, int id, String fileName, String storagePath) {
        InputStream inputStream = context.getResources().openRawResource(id);
        File file = new File(storagePath);
        if (!file.exists()) {//如果文件夹不存在，则创建新的文件夹
            file.mkdirs();
        }
        readInputStream(storagePath + SEPARATOR + fileName, inputStream);
    }

    /**
     * 读取输入流中的数据写入输出流
     *
     * @param storagePath 目标文件路径
     * @param inputStream 输入流
     */
    public static void readInputStream(String storagePath, InputStream inputStream) {
        File file = new File(storagePath);
        try {
            if (!file.exists()) {
                // 1.建立通道对象
                FileOutputStream fos = new FileOutputStream(file);
                // 2.定义存储空间
                byte[] buffer = new byte[inputStream.available()];
                // 3.开始读文件
                int lenght = 0;
                while ((lenght = inputStream.read(buffer)) != -1) {// 循环从输入流读取buffer字节
                    // 将Buffer中的数据写到outputStream对象中
                    fos.write(buffer, 0, lenght);
                }
                fos.flush();// 刷新缓冲区
                // 4.关闭流
                fos.close();
                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 格式化文件大小
     *
     * @param size
     * @return
     */
    public static String formatFileSize(long size) {
        return formatFileSize(size, 0, true);
    }

    /**
     * 文件大小转换
     *
     * @param number
     * @param pow        保留几位小数
     * @param isContainB 单位中是否带B
     * @return
     */
    public static String formatFileSize(long number, int pow, boolean isContainB) {
        String formatString = "##0";
        if (pow > 0) {
            formatString = formatString + ".";
            for (int i = 0; i < pow; i++) {
                formatString += "0";
            }
        }
        DecimalFormat df = new DecimalFormat(formatString);
        df.setRoundingMode(RoundingMode.DOWN);
        double point = Math.pow(10, pow);
        StringBuilder size = new StringBuilder();
        long oneKB = 1024; // 1KB
        long oneMB = 1024 * oneKB; // 1MB
        long oneGB = 1024 * oneMB;
        if (number <= 0) {
            size.append(isContainB ? "0KB" : "0K");
        } else if (number < oneKB) {
            size.append(isContainB ? "1KB" : "1K");
        } else if (number < oneMB) { // 小于一MB
            double numberDouble = number * 1.0d / oneKB * point / point;
            size.append(df.format(numberDouble)).append(isContainB ? "KB" : "K");
        } else if (number < oneGB) { // 小于一GB
            double numberDouble = number * 1.0d / oneMB * point / point;
            size.append(df.format(numberDouble)).append(isContainB ? "MB" : "M");
        } else {// 大于或者等于一GB
            double numberDouble = number * 1.0d / oneGB * point / point;
            size.append(df.format(numberDouble)).append(isContainB ? "GB" : "G");
        }
        return size.toString();
    }

}
