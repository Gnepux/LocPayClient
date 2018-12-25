package com.nuaa.locpayclient.utils;

import com.nuaa.locpayclient.MyApplication;

public class KeyUtils {

    public static void setPrivateKey(String id, String privateKey) {
        CUSPUtils.put(MyApplication.getInstance(), id + "-private", privateKey);
    }

    public static String getPrivateKey(String id) {
        return (String) CUSPUtils.get(MyApplication.getInstance(), id + "-private", "");
    }

    public static void setPublicKey(String id, String publicKey) {
        CUSPUtils.put(MyApplication.getInstance(), id + "-public", publicKey);
    }

    public static String getPublicKey(String id) {
        return (String) CUSPUtils.get(MyApplication.getInstance(), id+ "-public", "");
    }

}
