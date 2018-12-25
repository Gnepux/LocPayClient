package com.nuaa.locpayclient.permission;

import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * 权限管理类
 * Created by xupeng on 2017/9/1.
 */

public class PermissionManager {

//    PermissionManager.getInstance().setCustomAction().requestPermission(this, "需要权限" + weatherBean.getWeatherinfo().getCity(), new RequestPermCallback() {
//
//        @Override
//        protected void onRequest(CustomPermissionAction action) {
//            UniversalDialog.Builder builder = new UniversalDialog.Builder(MainActivity.this, UniversalDialog.Style.STYLE_NORMAL_TITLE);
//
//            builder.setTitle("温馨提示")
//                    .setMessage("需要申请摄像头权限")
//                    .setCanceledOnTouchOutSide(true)
//                    .setButtonTitle("取消", "开启")
//                    .setOnButtonClickListener(index -> {
//                        switch (index) {
//                            case 0:
//                                action.setGrant(false);
//                                break;
//                            case 1:
//                                action.setGrant(true);
//                                break;
//                        }
//                    })
//                    .setOnCancelListener(dialog -> action.setGrant(false)).show();
//        }
//
//        @Override
//        protected void onRequestRationale(CustomPermissionAction action) {
//            UniversalDialog.Builder builder = new UniversalDialog.Builder(MainActivity.this, UniversalDialog.Style.STYLE_NORMAL_TITLE);
//
//            builder.setTitle("温馨提示")
//                    .setMessage("需要申请摄像头权限")
//                    .setCanceledOnTouchOutSide(true)
//                    .setButtonTitle("确认不开启", "去设置")
//                    .setOnButtonClickListener(index -> {
//                        switch (index) {
//                            case 0:
//                                action.setRationaleGrant(false);
//                                break;
//                            case 1:
//                                action.setRationaleGrant(true);
//                                break;
//                        }
//                    })
//                    .setOnCancelListener(dialog -> action.setGrant(false))
//                    .show();
//        }
//
//        @Override
//        protected void onGrant() {
//            T.showShort(MainActivity.this, "允许");
//        }
//
//        @Override
//        protected void onDeny() {
//            T.showShort(MainActivity.this, "拒绝");
//        }
//    }, Manifest.permission.CAMERA);

    /**
     * Build.MANUFACTURER
     */
    private static final String MANUFACTURER_HUAWEI = "Huawei";//华为
    private static final String MANUFACTURER_MEIZU = "Meizu";//魅族
    private static final String MANUFACTURER_XIAOMI = "Xiaomi";//小米
    private static final String MANUFACTURER_SONY = "Sony";//索尼
    private static final String MANUFACTURER_OPPO = "OPPO";
    private static final String MANUFACTURER_LG = "LG";
    private static final String MANUFACTURER_VIVO = "vivo";
    private static final String MANUFACTURER_SAMSUNG = "samsung";//三星
    private static final String MANUFACTURER_LETV = "Letv";//乐视
    private static final String MANUFACTURER_LEMOBILE = "LeMobile";//乐视
    private static final String MANUFACTURER_ZTE = "ZTE";//中兴
    private static final String MANUFACTURER_YULONG = "YuLong";//酷派
    private static final String MANUFACTURER_LENOVO = "LENOVO";//联想

    private static final String ROM_MIUI_V5 = "V5";
    private static final String ROM_MIUI_V6 = "V6";
    private static final String ROM_MIUI_V7 = "V7";
    private static final String ROM_MIUI_V8 = "V8";
    private static final String ROM_MIUI_V9 = "V9";

    private static PermissionManager mInstance = null;

    private static String sApplicationId;

    private AlertDialog.Builder mDialogBuilder = null;

    private static CustomPermissionAction mAction = null;

    private Activity mActivity;

    private RequestPermCallback mCallback;

    public static void init(String applicationId) {
        sApplicationId = applicationId;
        getInstance();
    }

    public static PermissionManager getInstance() {
        if (mInstance == null) {
            synchronized (PermissionManager.class) {
                mInstance = new PermissionManager();
            }
        }
        mAction = null;
        return mInstance;
    }

    public PermissionManager setCustomAction() {
        PermissionManager permissionManager = getInstance();
        mAction = new CustomPermissionAction();
        return permissionManager;
    }

    private PermissionManager() {
    }

    protected void goSettingAction(Observer<? super Boolean> observer) {
        goToSetting(mActivity);
        if (mCallback != null) {
            mCallback.onSetting();
        }
        observer.onComplete();
    }

    public void requestPermission(final FragmentActivity activity, final String desc, final RequestPermCallback callback, final String... permissions) {
        mActivity = activity;
        mCallback = callback;

        try {
            final RxPermissions rxPermissions = new RxPermissions(activity);
            rxPermissions.request(permissions)
                    .flatMap(new Function<Boolean, ObservableSource<Boolean>>() {
                        @Override
                        public ObservableSource<Boolean> apply(Boolean grant) {
                            if (grant) {
                                if (callback != null) {
                                    callback.onGrant();
                                }
                                return Observable.empty();
                            } else {
                                return rxPermissions.shouldShowRequestPermissionRationale(activity, permissions);
                            }
                        }
                    })
                    .flatMap(new Function<Boolean, ObservableSource<Boolean>>() {
                        @Override
                        public ObservableSource<Boolean> apply(final Boolean show) {
                            return new Observable<Boolean>() {
                                @Override
                                protected void subscribeActual(final Observer<? super Boolean> observer) {
                                    if (show) {
                                        if (mAction != null) {
                                            mAction.mObserver = observer;
                                            callback.onRequest(mAction);
                                        } else {
                                            obtainDialogBuilder(activity)
                                                    .setTitle("提示")
                                                    .setMessage(desc)
                                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            observer.onNext(false);
                                                        }
                                                    })
                                                    .setPositiveButton("开启", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            observer.onNext(true);
                                                        }
                                                    })
                                                    .show();
                                        }
                                    } else {
                                        if (mAction != null) {
                                            mAction.mObserver = observer;
                                            mAction.mManager = PermissionManager.this;
                                            callback.onRequestRationale(mAction);
                                        } else {
                                            obtainDialogBuilder(activity)
                                                    .setTitle("提示")
                                                    .setMessage(desc)
                                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            observer.onNext(false);
                                                        }
                                                    })
                                                    .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            goSettingAction(observer);
                                                        }
                                                    })
                                                    .show();
                                        }
                                    }
                                }
                            };
                        }
                    }).flatMap(new Function<Boolean, ObservableSource<Boolean>>() {
                @Override
                public ObservableSource<Boolean> apply(Boolean request) {
                    if (request) {
                        return rxPermissions.request(permissions);
                    } else {
                        return Observable.just(false);
                    }
                }
            }).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean granted) {
                    if (callback != null) {
                        if (granted) {
                            callback.onGrant();
                        } else {
                            callback.onDeny();
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private AlertDialog.Builder obtainDialogBuilder(Activity activity) {
        if (mDialogBuilder == null) {
            mDialogBuilder = new AlertDialog.Builder(activity);
        }
        return mDialogBuilder;
    }

    /**
     * 此函数可以自己定义
     *
     * @param activity
     */
    private void goToSetting(Activity activity) {
        Intent intent;
        String manufacturer = Build.MANUFACTURER;
        if (MANUFACTURER_HUAWEI.equalsIgnoreCase(manufacturer)) {
            intent = Huawei(activity);
        } else if (MANUFACTURER_MEIZU.equalsIgnoreCase(manufacturer)) {
            intent = Meizu(activity);
        } else if (MANUFACTURER_XIAOMI.equalsIgnoreCase(manufacturer)) {
            intent = Xiaomi(activity);
        } else if (MANUFACTURER_SONY.equalsIgnoreCase(manufacturer)) {
            intent = Sony(activity);
        } else if (MANUFACTURER_OPPO.equalsIgnoreCase(manufacturer)) {
            intent = OPPO(activity);
        } else if (MANUFACTURER_LG.equalsIgnoreCase(manufacturer)) {
            intent = LG(activity);
        } else if (MANUFACTURER_LETV.equalsIgnoreCase(manufacturer)
                || MANUFACTURER_LEMOBILE.equalsIgnoreCase(manufacturer)) {
            intent = Letv(activity);
        } else if (MANUFACTURER_VIVO.equalsIgnoreCase(manufacturer)) {
            intent = Vivo(activity);
        } else {
            intent = ApplicationInfo(activity);
        }

        if (intent.resolveActivity(activity.getPackageManager()) == null) {
            intent = ApplicationInfo(activity);
        }
        activity.startActivity(intent);
    }

    private Intent Huawei(Activity activity) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", sApplicationId);
        ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");
        intent.setComponent(comp);
        return intent;
    }

    private Intent Meizu(Activity activity) {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra("packageName", sApplicationId);
        return intent;
    }

    private Intent Xiaomi(Activity activity) {
        String rom = getMiuiVersion();
        Intent intent = null;
        if (ROM_MIUI_V5.equals(rom)) {
            Uri packageURI = Uri.parse("package:" + activity.getApplicationInfo().packageName);
            intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
        } else if (ROM_MIUI_V6.equals(rom) || ROM_MIUI_V7.equals(rom)) {
            intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
            intent.putExtra("extra_pkgname", activity.getPackageName());
        } else if (ROM_MIUI_V8.equals(rom) || ROM_MIUI_V9.equals(rom)) {
            intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
            intent.putExtra("extra_pkgname", activity.getPackageName());
        } else {
            intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
            intent.putExtra("extra_pkgname", activity.getPackageName());
        }
        return intent;
    }

    // 获取MIUI系统版本
    private static String getMiuiVersion() {
        String propName = "ro.miui.ui.version.name";
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(
                    new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            Log.i("PermissionManager", "Unable to read sysprop " + propName);
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return line;
    }

    private Intent Sony(Activity activity) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", sApplicationId);
        ComponentName comp = new ComponentName("com.sonymobile.cta", "com.sonymobile.cta.SomcCTAMainActivity");
        intent.setComponent(comp);
        return intent;
    }

    private Intent OPPO(Activity activity) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", sApplicationId);
        ComponentName comp = new ComponentName("com.color.safecenter", "com.color.safecenter.permission.PermissionManagerActivity");
        intent.setComponent(comp);
        return intent;
    }

    private Intent LG(Activity activity) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", sApplicationId);
        ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.Settings$AccessLockSummaryActivity");
        intent.setComponent(comp);
        return intent;
    }

    private Intent Letv(Activity activity) {
        // 乐视手机进应用主页设置权限比较方便
        return ApplicationInfo(activity);

//        Intent intent = new Intent();
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
//        ComponentName comp = new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.PermissionAndApps");
//        intent.setComponent(comp);
//        return intent;
    }

    private Intent Vivo(Activity activity) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", sApplicationId);
        ComponentName comp = new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.PurviewTabActivity");
        intent.setComponent(comp);
        return intent;
    }

    /**
     * 只能打开到自带安全软件
     *
     * @param activity
     */
    private Intent _360(Activity activity) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", sApplicationId);
        ComponentName comp = new ComponentName("com.qihoo360.mobilesafe", "com.qihoo360.mobilesafe.ui.index.AppEnterActivity");
        intent.setComponent(comp);
        return intent;
    }

    /**
     * 应用信息界面
     *
     * @param activity
     */
    private Intent ApplicationInfo(Activity activity) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", activity.getPackageName());
        }
        return localIntent;
    }

}
