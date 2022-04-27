package com.hatsukoi.eshopblvd.member.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;

/**
 * 用户密码加密工具类
 * @author gaoweilin
 * @date 2022/04/28 Thu 3:19 AM
 */
public class EncryptUtils {
    /**
     * 返回加盐值的md5加密
     * @param rawPassword
     * @param salt
     * @return
     */
    public static String encryptWithSalt(String rawPassword, Long salt) {
        return Md5Crypt.md5Crypt(rawPassword.getBytes(), "$1$" + salt.toString());
    }

    /**
     * 密码验证
     * @param rawPassword
     * @param salt
     * @param encryptedPassword
     * @return
     */
    public static boolean verify(String rawPassword, Long salt, String encryptedPassword) {
        return encryptedPassword.equals(Md5Crypt.md5Crypt(rawPassword.getBytes(), "$1$" + salt.toString()));
    }
}
