package com.nuaa.locpayclient.model;

public class UserRegisterRequest {

    private String aid;

    private String account;

    private String publicKey;

    public UserRegisterRequest(String aid, String account, String publicKey) {
        this.aid = aid;
        this.account = account;
        this.publicKey = publicKey;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
