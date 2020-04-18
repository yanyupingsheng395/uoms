package com.linksteady.wxofficial.common.util;

import lombok.SneakyThrows;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OkHttpUtil {

    private static OkHttpClient okHttpClient;

    static {
        okHttpClient = new OkHttpClient.Builder()
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
                .url(url).get()
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
     * @param param
     * @return
     */
    @SneakyThrows
    public static String postRequestBody(String url, String param) {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), param.getBytes()))
                .build();

        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        ResponseBody responseBody = response.body();
        return responseBody.string();
    }

    @SneakyThrows
    public static String postFormBody(String url, Map<String, String> param) {
        OkHttpClient okHttpClient = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        param.forEach(builder::add);
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder().post(requestBody).url(url)
                .post(requestBody).build();
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


    /**
     * 提交数据和文件
     * @param url
     * @param map
     * @param file
     * @return
     * @throws Exception
     */
    public static String postFileAndData(String url, Map<String,String> map, File file) throws Exception{
        // form 表单形式上传
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if(file != null){
            // MediaType.parse() 里面是上传的文件类型。
            RequestBody body = RequestBody.create(MediaType.parse("image/*"), file);
            String filename = file.getName();
            // 参数分别为， 请求key ，文件名称 ， RequestBody
            requestBody.addFormDataPart("file", file.getName(), body);
        }
        if (map != null) {
            // map 里面是请求中所需要的 key 和 value
            for (Map.Entry<String,String> entry : map.entrySet()) {
                requestBody.addFormDataPart(entry.getKey(),entry.getValue());
            }
        }
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "text/plain")
                .post(requestBody.build())
                .build();

        OkHttpClient okHttpClient = new OkHttpClient();
        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        ResponseBody responseBody = response.body();
        return responseBody.string();
    }
}
