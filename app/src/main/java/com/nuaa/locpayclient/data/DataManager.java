package com.nuaa.locpayclient.data;

import com.nuaa.locpayclient.model.BaseResponse;
import com.nuaa.locpayclient.model.EmptyResponse;
import com.nuaa.locpayclient.model.UserRegisterRequest;

import io.reactivex.Observable;

public class DataManager {

    private static DataManager sDataManager = null;

    private IDataSource mDataSource;

    public DataManager(IDataSource dataSource) {
        this.mDataSource = dataSource;
    }

    public static DataManager getInstance() {
        if (sDataManager == null) {
            synchronized (DataManager.class) {
                if (sDataManager == null) {
                    sDataManager = new DataManager(RetrofitServiceUtil.create());
                }
            }
        }
        return sDataManager;
    }

    public Observable<BaseResponse<EmptyResponse>> userRegister(String aid, String account, String publicKey) {
        return mDataSource.userRegister(new UserRegisterRequest(aid, account, publicKey));
    }

}
