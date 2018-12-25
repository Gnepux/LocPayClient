package com.nuaa.locpayclient.data;

import com.nuaa.locpayclient.model.BaseResponse;
import com.nuaa.locpayclient.model.EmptyResponse;
import com.nuaa.locpayclient.model.UserRegisterRequest;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IDataSource {

    @POST("/business/userRegister/")
    Observable<BaseResponse<EmptyResponse>> userRegister(@Body UserRegisterRequest request);

}
