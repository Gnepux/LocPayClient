package com.nuaa.locpayclient.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.nuaa.locpayclient.R;
import com.nuaa.locpayclient.model.QRCodeBean;
import com.nuaa.locpayclient.utils.EncodingUtils;
import com.nuaa.locpayclient.utils.KeyUtils;
import com.nuaa.locpayclient.utils.RSAUtil;

public class PayActivity extends AppCompatActivity {

    private EditText mAidEt;

    private ImageView mPayQrCodeIv;

    private TextView mCreateQrTimeTv;

    private BDLocation mLocation = null;

    public LocationClient mLocationClient = null;

    private MyLocationListener myListener = new MyLocationListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        mAidEt = findViewById(R.id.et_aid);
        mPayQrCodeIv = findViewById(R.id.iv_pay_qr_code);
        mCreateQrTimeTv = findViewById(R.id.tv_create_qr_time);

        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数

        LocationClientOption option = new LocationClientOption();

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
//可选，设置定位模式，默认高精度
//LocationMode.Hight_Accuracy：高精度；
//LocationMode. Battery_Saving：低功耗；
//LocationMode. Device_Sensors：仅使用设备；

//        option.setCoorType("bd09ll");
//可选，设置返回经纬度坐标类型，默认GCJ02
//GCJ02：国测局坐标；
//BD09ll：百度经纬度坐标；
//BD09：百度墨卡托坐标；
//海外地区定位，无需设置坐标类型，统一返回WGS84类型坐标

        option.setScanSpan(1000);
//可选，设置发起定位请求的间隔，int类型，单位ms
//如果设置为0，则代表单次定位，即仅定位一次，默认为0
//如果设置非0，需设置1000ms以上才有效

        option.setOpenGps(true);
//可选，设置是否使用gps，默认false
//使用高精度和仅用设备两种定位模式的，参数必须设置为true

        option.setLocationNotify(true);
//可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

        option.setIgnoreKillProcess(false);
//可选，定位SDK内部是一个service，并放到了独立进程。
//设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

        option.SetIgnoreCacheException(false);
//可选，设置是否收集Crash信息，默认收集，即参数为false

        option.setWifiCacheTimeOut(5 * 60 * 1000);
//可选，V7.2版本新增能力
//如果设置了该接口，首次启动定位时，会先判断当前Wi-Fi是否超出有效期，若超出有效期，会先重新扫描Wi-Fi，然后定位

        option.setEnableSimulateGps(false);
//可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false

        mLocationClient.setLocOption(option);
//mLocationClient为第二步初始化过的LocationClient对象
//需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
//更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明

        mLocationClient.start();
//mLocationClient为第二步初始化过的LocationClient对象
//调用LocationClient的start()方法，便可发起定位请求
    }

    public void pay(View v) {
        final String aid = mAidEt.getText().toString();
        if (TextUtils.isEmpty(aid)) {
            mAidEt.requestFocus();
            mAidEt.setError("请输入AID");
            return;
        }

        if (mLocation == null) {
            Toast.makeText(this, "暂未获取当前位置,请重试", Toast.LENGTH_SHORT).show();
            return;
        }

        long time = System.currentTimeMillis();

        try {

            double lon = mLocation.getLongitude();

            double lat = mLocation.getLatitude();

            String userMsg = aid + lon + "," + lat + time;

            String userMsgSign = RSAUtil.sign(KeyUtils.getPrivateKey(aid), userMsg);

            QRCodeBean qrCodeBean = new QRCodeBean(aid, time, lon, lat, userMsgSign);
            Gson gson = new Gson();
            String qrCodeStr = gson.toJson(qrCodeBean);

            long time1 = System.currentTimeMillis();
            Bitmap bitmap = EncodingUtils.createQRCode(qrCodeStr, mPayQrCodeIv.getWidth(), mPayQrCodeIv.getHeight());
            long time2 = System.currentTimeMillis();
            mCreateQrTimeTv.setText("生成二维码时间:" + (time2 - time1) + "毫秒");
            mPayQrCodeIv.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "生成付款码出错", Toast.LENGTH_SHORT).show();
        }
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            double latitude = location.getLatitude();    //获取纬度信息
            double longitude = location.getLongitude();    //获取经度信息
            float radius = location.getRadius();    //获取定位精度，默认值为0.0f

            String coorType = location.getCoorType();
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准

            int errorCode = location.getLocType();
            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明

            mLocation = location;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocationClient != null) {
            mLocationClient.stop();
        }

    }
}
