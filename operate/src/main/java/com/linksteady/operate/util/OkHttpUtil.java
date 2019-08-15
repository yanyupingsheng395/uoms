package com.linksteady.operate.util;

import lombok.SneakyThrows;
import okhttp3.*;

import java.util.concurrent.TimeUnit;

public class OkHttpUtil {

    /**
     * 调用一次get请求
     * @param url
     * @return
     */
    @SneakyThrows
    public static String callTextPlain(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = buildOkHttpClient().newCall(request);
        Response response = call.execute();
        ResponseBody responseBody = response.body();
        return responseBody.string();
    }

    /**
     * 调用一次post请求
     * @param url
     * @param value
     * @return
     */
    @SneakyThrows
    public static String callTextPlainPost(String url,String value) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "text/plain")
                .post(RequestBody.create(MediaType.parse("text/plain"), value.getBytes()))
                .build();

        Call call = buildOkHttpClient().newCall(request);
        Response response = call.execute();
        ResponseBody responseBody = response.body();
        return responseBody.string();
    }

    /**
     * 调用一次get请求，使用basic认证
     * @param url
     * @return
     */
    @SneakyThrows
    public static String callTextPlainWithBasicAuth(String url,String username,String password) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "text/plain")
                .addHeader("Authorization", Credentials.basic(username, password))
                .build();

        Call call = buildOkHttpClient().newCall(request);
        Response response = call.execute();
        ResponseBody responseBody = response.body();
        return responseBody.string();
    }

    /**
     * 调用一次post请求，使用basic认证
     * @param url
     * @return
     */
    @SneakyThrows
    public static String callPostWithBasicAuth(String url,String username,String password,String value) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "text/plain")
                .addHeader("Authorization", Credentials.basic(username, password))
                .post(RequestBody.create(MediaType.parse("text/plain"), value.getBytes()))
                .build();

        Call call = buildOkHttpClient().newCall(request);
        Response response = call.execute();
        ResponseBody responseBody = response.body();
        return responseBody.string();
    }

    public static OkHttpClient buildOkHttpClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.SECONDS)
                .writeTimeout(2, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
        return client;
    }
}
