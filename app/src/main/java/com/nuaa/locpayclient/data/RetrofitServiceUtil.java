package com.nuaa.locpayclient.data;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitServiceUtil {

    private static Retrofit generateRetrofitBuilder() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(loggingInterceptor);

        // 请求拦截
        builder.connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);

        // Stetho拦截器
        builder.addNetworkInterceptor(new StethoInterceptor());

        OkHttpClient client = builder.build();

        // Gson配置
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .create();

        return new Retrofit.Builder()
                .client(client)
                .baseUrl(ServerConfig.getBaseUrl())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static IDataSource create() {
        return generateRetrofitBuilder().create(IDataSource.class);
    }
}
