package com.nuaa.locpayclient.utils;

import org.apache.commons.codec.binary.Base64;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;

public class RSAUtil {
    /**
     * 指定加密算法为RSA
     */
    private static final String ALGORITHM = "RSA";
    /**
     * 密钥长度，用来初始化
     */
    private static final int KEYSIZE = 1024;

    public RSAUtil() {
    }

    /**
     * 使用私钥加密数据 用一个已打包成byte[]形式的私钥加密数据，即数字签名
     *
     * @param key    打包成byte[]的私钥
     * @param source 要签名的数据，一般应是数字摘要
     * @return 签名 str
     */
    public static String sign(String key, String source) {
        try {
            byte[] keyInByte = decodeBase64(key);

            byte[] sourceInByte = MdigestSHA(source);
            PKCS8EncodedKeySpec priv_spec = new PKCS8EncodedKeySpec(keyInByte);
            KeyFactory mykeyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privKey = mykeyFactory.generatePrivate(priv_spec);
            Signature sig = Signature.getInstance("SHA1withRSA");
            sig.initSign(privKey);
            sig.update(sourceInByte);
            return encodeBase64URLSafeString(sig.sign());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 验证数字签名
     *
     * @param key       打包成byte[]形式的公钥
     * @param sourceStr 原文的数字摘要
     * @param signStr   签名（对原文的数字摘要的签名）
     * @return 是否证实 boolean
     */
    public static boolean verify(String key, String sourceStr, String signStr) {
        try {
            byte[] keyInByte = Base64.decodeBase64(key);
            byte[] source = MdigestSHA(sourceStr);
            byte[] sign = Base64.decodeBase64(signStr);
            KeyFactory mykeyFactory = KeyFactory.getInstance("RSA");
            Signature sig = Signature.getInstance("SHA1withRSA");
            X509EncodedKeySpec pub_spec = new X509EncodedKeySpec(keyInByte);
            PublicKey pubKey = mykeyFactory.generatePublic(pub_spec);
            sig.initVerify(pubKey);
            sig.update(source);
            return sig.verify(sign);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 使用RSA公钥加密数据
     *
     * @param pubKeyInByte 打包的byte[]形式公钥
     * @param data         要加密的数据
     * @return 加密数据
     */
    public static byte[] encryptByRSA(byte[] pubKeyInByte, byte[] data) {
        try {
            KeyFactory mykeyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec pub_spec = new X509EncodedKeySpec(pubKeyInByte);
            PublicKey pubKey = mykeyFactory.generatePublic(pub_spec);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 用RSA私钥解密
     *
     * @param privKeyInByte 私钥打包成byte[]形式
     * @param data          要解密的数据
     * @return 解密数据
     */
    public static byte[] decryptByRSA(byte[] privKeyInByte, byte[] data) {
        try {
            PKCS8EncodedKeySpec priv_spec = new PKCS8EncodedKeySpec(privKeyInByte);
            KeyFactory mykeyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privKey = mykeyFactory.generatePrivate(priv_spec);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * 使用RSA私钥加密数据
     *
     * @param privKeyInByte 打包的byte[]形式私钥
     * @param data          要加密的数据
     * @return 加密数据
     */
    public static byte[] encryptByRSA1(byte[] privKeyInByte, byte[] data) {
        try {
            PKCS8EncodedKeySpec priv_spec = new PKCS8EncodedKeySpec(privKeyInByte);
            KeyFactory mykeyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privKey = mykeyFactory.generatePrivate(priv_spec);
            Cipher cipher = Cipher.getInstance(mykeyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, privKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * 用RSA公钥解密
     *
     * @param pubKeyInByte 公钥打包成byte[]形式
     * @param data         要解密的数据
     * @return 解密数据
     */
    public static byte[] decryptByRSA1(byte[] pubKeyInByte, byte[] data) {
        try {
            KeyFactory mykeyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec pub_spec = new X509EncodedKeySpec(pubKeyInByte);
            PublicKey pubKey = mykeyFactory.generatePublic(pub_spec);
            Cipher cipher = Cipher.getInstance(mykeyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, pubKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 计算字符串的SHA数字摘要，以byte[]形式返回
     */
    public static byte[] MdigestSHA(String source) {
        // byte[] nullreturn = { 0 };
        try {
            MessageDigest thisMD = MessageDigest.getInstance("SHA");
            byte[] digest = thisMD.digest(source.getBytes("UTF-8"));
            return digest;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 生成秘钥对
     *
     * @param userName 0-公钥 1-私钥
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String[] genKeyPair(String userName) throws NoSuchAlgorithmException {

        /** RSA算法要求有一个可信任的随机数源 */
        SecureRandom secureRandom = new SecureRandom(userName.getBytes());

        /** 为RSA算法创建一个KeyPairGenerator对象 */
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);

        /** 利用上面的随机数据源初始化这个KeyPairGenerator对象 */
        keyPairGenerator.initialize(KEYSIZE, secureRandom);
        //keyPairGenerator.initialize(KEYSIZE);

        /** 生成密匙对 */
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        /** 得到公钥 */
        Key publicKey = keyPair.getPublic();

        /** 得到私钥 */
        Key privateKey = keyPair.getPrivate();

        byte[] publicKeyBytes = publicKey.getEncoded();
        byte[] privateKeyBytes = privateKey.getEncoded();

        String publicKeyBase64 = encodeBase64URLSafeString(publicKeyBytes);
        String privateKeyBase64 = encodeBase64URLSafeString(privateKeyBytes);
        String[] str = {publicKeyBase64, privateKeyBase64};
        return str;
    }

    /**
     * 测试
     *
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        try {

            String[] str = genKeyPair("xupeng");
            byte[] signPrivate = Base64.decodeBase64(str[1]);
            byte[] signPublic = Base64.decodeBase64(str[0]);

            // 使用私钥对摘要进行加密 获得密文 即数字签名
            String sign = sign(str[1], "假设这是要加密的客户数据");
            // 使用公钥对密文进行解密,解密后与摘要进行匹配
            boolean yes = verify(str[0], "假设这是要加密的客户数据", sign);
            if (yes) {
                System.out.println("匹配成功 合法的签名!");
            }
            // 公钥加密私钥解密
            // 获得摘要
            byte[] sourcepub_pri = ("13265986584||316494646546486498||01||public").getBytes("UTF-8");

            // 使用公钥对摘要进行加密 获得密文
            byte[] signpub_pri = encryptByRSA(signPublic, sourcepub_pri);

            // 使用私钥对密文进行解密 返回解密后的数据
            byte[] newSourcepub_pri = decryptByRSA(signPrivate, signpub_pri);

            System.out.println("私钥解密：" + new String(newSourcepub_pri, "UTF-8"));
            // 对比源数据与解密后的数据
            if (Arrays.equals(sourcepub_pri, newSourcepub_pri)) {
                System.out.println("匹配成功 合法的私钥!");
            }

            // 私钥加密公钥解密
            byte[] sourcepri_pub = ("13265986584||316494646546486498||01||private").getBytes("UTF-8");

            // 使用私钥对摘要进行加密 获得密文
            byte[] signpri_pub = encryptByRSA1(signPrivate, sourcepri_pub);

            // 使用公钥对密文进行解密 返回解密后的数据
            byte[] newSourcepri_pub = decryptByRSA1(signPublic, signpri_pub);

            System.out.println("公钥解密：" + new String(newSourcepri_pub, "UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();
        }
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
