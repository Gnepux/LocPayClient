package com.nuaa.locpayclient.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.nuaa.locpayclient.R;
import com.nuaa.locpayclient.permission.PermissionManager;
import com.nuaa.locpayclient.permission.RequestPermCallback;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void pay(View view) {
        PermissionManager.getInstance().requestPermission(this, "需开启定位权限", new RequestPermCallback() {
            @Override
            protected void onGrant() {
                Intent intent = new Intent(MainActivity.this, PayActivity.class);
                startActivity(intent);
            }

            @Override
            protected void onDeny() {
                Toast.makeText(MainActivity.this, "开启定位权限失败", Toast.LENGTH_SHORT).show();
            }
        }, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
    }
}
