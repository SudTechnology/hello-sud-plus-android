package tech.sud.mgp.hello.ui.login;

import androidx.annotation.NonNull;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tech.sud.mgp.hello.service.main.config.SudEnvConfig;

public class DeveloperKitUtils {

    /** 随机生成一个userId，用于演示 */
    public static long genUserID() {
        return new Random().nextInt(Integer.MAX_VALUE);
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

    @NonNull
    public static List<SudEnvConfig> getSudEnvConfigs() {
        List<SudEnvConfig> list = new ArrayList<>();
        list.add(buildSudEnvConfig("Dev", true, 4));
        list.add(buildSudEnvConfig("Fat", true, 3));
        list.add(buildSudEnvConfig("Sim", true, 2));
        list.add(buildSudEnvConfig("Pro", false, 1));
        return list;
    }

    public static SudEnvConfig buildSudEnvConfig(String name, boolean isTestEnv, int env) {
        SudEnvConfig config = new SudEnvConfig();
        config.name = name;
        config.isTestEnv = isTestEnv;
        config.env = env;
        return config;
    }

}
