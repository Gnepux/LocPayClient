package com.nuaa.locpayclient.permission;

import android.support.annotation.Nullable;

/**
 * Created by xupeng on 2017/12/18.
 */
public abstract class RequestPermCallback {

    // 允许
    protected abstract void onGrant();

    // 拒绝
    protected abstract void onDeny();

    // 第一次讯问时候的回到
    protected void onRequest(@Nullable CustomPermissionAction action) {}

    // 不再提醒时候的回调
    protected void onRequestRationale(@Nullable CustomPermissionAction action) {}

    // 去设置时触发
    protected void onSetting() {}
}
