/*
 * Copyright © Sud.Tech
 * https://sud.tech
 */

package tech.sud.mgp.hello.common.utils;


import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * @author guanghui
 * Created on 2021/12/4
 */
public class UserUtils {

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

}
