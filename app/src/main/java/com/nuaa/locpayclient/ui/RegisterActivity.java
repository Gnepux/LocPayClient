package com.nuaa.locpayclient.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.nuaa.locpayclient.R;
import com.nuaa.locpayclient.constant.Constant;
import com.nuaa.locpayclient.data.DataManager;
import com.nuaa.locpayclient.model.BaseResponse;
import com.nuaa.locpayclient.model.EmptyResponse;
import com.nuaa.locpayclient.model.HeaderModel;
import com.nuaa.locpayclient.utils.KeyUtils;
import com.nuaa.locpayclient.utils.RSAUtil;

import java.security.NoSuchAlgorithmException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class RegisterActivity extends AppCompatActivity {

    private EditText mAidEt;

    private EditText mAccountEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAidEt = findViewById(R.id.et_aid);
        mAccountEt = findViewById(R.id.et_account);
    }

    public void register(View v) {
        String aid = mAidEt.getText().toString();
        if (TextUtils.isEmpty(aid)) {
            mAidEt.requestFocus();
            mAidEt.setError("请输入aid");
            return;
        }

        String account = mAccountEt.getText().toString();
        if (TextUtils.isEmpty(account)) {
            mAccountEt.requestFocus();
            mAccountEt.setError("请输入账户");
            return;
        }

        try {
            String[] keyPair = RSAUtil.genKeyPair(aid);
            KeyUtils.setPrivateKey(aid, keyPair[1]);
            KeyUtils.setPublicKey(aid, keyPair[0]);
            DataManager.getInstance().userRegister(aid, account, keyPair[0])
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<BaseResponse<EmptyResponse>>() {
                        @Override
                        public void accept(BaseResponse<EmptyResponse> response) {
                            HeaderModel headerModel = response.getHeader();
                            if (Constant.OK.equals(headerModel.getErrorCode())) {
                                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this, headerModel.getErrorMsg(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            Toast.makeText(RegisterActivity.this, "网络错误:" + throwable.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Toast.makeText(this, "注册出错", Toast.LENGTH_SHORT).show();
        }
    }
}
