package com.linksteady.wxofficial.common.util;

import lombok.SneakyThrows;
import okhttp3.*;

import java.util.concurrent.TimeUnit;

public class OkHttpUtil {

    private static OkHttpClient okHttpClient;

    static {
        okHttpClient =  new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.SECONDS)
                .writeTimeout(2, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
    }

    public static OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    /**
     * 调用一次get请求
     *
     * @param url
     * @return
     */
    @SneakyThrows
    public static String callTextPlain(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        ResponseBody responseBody = response.body();
        return responseBody.string();
    }

    /**
     * 调用一次post请求
     *
     * @param url
     * @param value
     * @return
     */
    @SneakyThrows
    public static String callTextPlainPost(String url, String value) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "text/plain")
                .post(RequestBody.create(MediaType.parse("text/plain"), value.getBytes()))
                .build();

        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        ResponseBody responseBody = response.body();
        return responseBody.string();
    }

    /**
     * 调用一次get请求，使用basic认证
     *
     * @param url
     * @return
     */
    @SneakyThrows
    public static String callTextPlainWithBasicAuth(String url, String username, String password) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "text/plain")
                .addHeader("Authorization", Credentials.basic(username, password))
                .build();

        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        ResponseBody responseBody = response.body();
        return responseBody.string();
    }

    /**
     * 调用一次post请求，使用basic认证
     *
     * @param url
     * @return
     */
    @SneakyThrows
    public static String callPostWithBasicAuth(String url, String username, String password, String value) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "text/plain")
                .addHeader("Authorization", Credentials.basic(username, password))
                .post(RequestBody.create(MediaType.parse("text/plain"), value.getBytes()))
                .build();

        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        ResponseBody responseBody = response.body();
        return responseBody.string();
    }

}
