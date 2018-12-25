package com.nuaa.locpayclient.utils;

import android.util.Base64;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSAUtil4Android {

    public static final String RSA = "RSA";// 非对称加密密钥算法
    public static final String ECB_PKCS1_PADDING = "RSA/ECB/PKCS1Padding";//加密填充方式
    public static final int DEFAULT_KEY_SIZE = 2048;//秘钥默认长度
    public static final byte[] DEFAULT_SPLIT = "#PART#".getBytes();    // 当要加密的内容超过bufferSize，则采用partSplit进行分块加密
    public static final int DEFAULT_BUFFERSIZE = (DEFAULT_KEY_SIZE / 8) - 11;// 当前秘钥支持加密的最大字节数

    /**
     * 随机生成RSA密钥对
     *
     * 密钥长度，范围：512～2048
     *                  一般1024
     * @return
     */
    public static String[] generateRSAKeyPair(String userName) {
        try {
            SecureRandom secureRandom = new SecureRandom(userName.getBytes());

            KeyPairGenerator kpg = KeyPairGenerator.getInstance(RSA);
            kpg.initialize(1024, secureRandom);
            KeyPair keyPair = kpg.genKeyPair();
            Key publicKey = keyPair.getPublic();
            Key privateKey = keyPair.getPrivate();

            byte[] publicKeyBytes = publicKey.getEncoded();
            byte[] privateKeyBytes = privateKey.getEncoded();

            String publicKeyBase64 = encodeBase64URLSafeString(publicKeyBytes);
            String privateKeyBase64 = encodeBase64URLSafeString(privateKeyBytes);
            String[] str = {publicKeyBase64, privateKeyBase64};
            return str;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 用公钥对字符串进行加密
     *
     * @param data 原文
     */
    public static String encryptByPublicKey(String data, String publicKey) throws Exception {
        byte[] keyInByte = decodeBase64(publicKey);
        // 得到公钥
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyInByte);
        KeyFactory kf = KeyFactory.getInstance(RSA);
        PublicKey keyPublic = kf.generatePublic(keySpec);
        // 加密数据
        Cipher cp = Cipher.getInstance(ECB_PKCS1_PADDING);
        cp.init(Cipher.ENCRYPT_MODE, keyPublic);
        return new String(cp.doFinal(data.getBytes()));
    }

    /**
     * 私钥加密
     *
     * @param data       待加密数据
     * @param privateKey 密钥
     * @return byte[] 加密数据
     */
    public static String encryptByPrivateKey(String data, String privateKey) throws Exception {
        byte[] keyInByte = decodeBase64(privateKey);
        // 得到私钥
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyInByte);
        KeyFactory kf = KeyFactory.getInstance(RSA);
        PrivateKey keyPrivate = kf.generatePrivate(keySpec);
        // 数据加密
        Cipher cipher = Cipher.getInstance(ECB_PKCS1_PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, keyPrivate);
        return encodeBase64URLSafeString(cipher.doFinal(data.getBytes()));
    }

    /**
     * 公钥解密
     *
     * @param data      待解密数据
     * @param publicKey 密钥
     * @return byte[] 解密数据
     */
    public static byte[] decryptByPublicKey(byte[] data, byte[] publicKey) throws Exception {
        // 得到公钥
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
        KeyFactory kf = KeyFactory.getInstance(RSA);
        PublicKey keyPublic = kf.generatePublic(keySpec);
        // 数据解密
        Cipher cipher = Cipher.getInstance(ECB_PKCS1_PADDING);
        cipher.init(Cipher.DECRYPT_MODE, keyPublic);
        return cipher.doFinal(data);
    }

    /**
     * 使用私钥进行解密
     */
    public static byte[] decryptByPrivateKey(byte[] encrypted, byte[] privateKey) throws Exception {
        // 得到私钥
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);
        KeyFactory kf = KeyFactory.getInstance(RSA);
        PrivateKey keyPrivate = kf.generatePrivate(keySpec);

        // 解密数据
        Cipher cp = Cipher.getInstance(ECB_PKCS1_PADDING);
        cp.init(Cipher.DECRYPT_MODE, keyPrivate);
        byte[] arr = cp.doFinal(encrypted);
        return arr;
    }

    // decode data from base 64
    private static byte[] decodeBase64(String dataToDecode) {
        byte[] dataDecoded = android.util.Base64.decode(dataToDecode, android.util.Base64.DEFAULT);
        return dataDecoded;
    }

    //enconde data in base 64
    private static byte[] encodeBase64(byte[] dataToEncode) {
        byte[] dataEncoded = android.util.Base64.encode(dataToEncode, android.util.Base64.DEFAULT);
        return dataEncoded;
    }

    private static String encodeBase64URLSafeString(byte[] binaryData) {
        return android.util.Base64.encodeToString(binaryData, android.util.Base64.DEFAULT);
    }
}
